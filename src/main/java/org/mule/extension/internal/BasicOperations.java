package org.mule.extension.internal;

import static org.mule.extension.internal.BasicConnection.convertStreamToString;
import static org.mule.extension.internal.SlackEndPoints.*;

import org.json.JSONObject;
import org.mule.runtime.api.el.BindingContext;
import org.mule.runtime.api.el.ExpressionLanguage;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.io.StringWriter;


public class BasicOperations {

    @Inject
    ExpressionLanguage expressionLanguage;

    @MediaType("application/json")
    @Throws(ExecuteErrorsProvider.class)
    public InputStream getChannels(@Config BasicConfiguration configuration, @Connection BasicConnection connection) {
        try {
            HttpResponse response = connection.doRequest(CONVERSATION_LIST, BASE_URI, HttpConstants.Method.GET, "",null);

            boolean okResult = evalauteExpression(response.getEntity().getContent(),"#[payload.ok]");

            if(okResult)
            {
                return response.getEntity().getContent();
            }
            throw new IllegalStateException();
        } catch (IllegalStateException e) {
            throw new ModuleException(SlackErrors.BAD_CREDENTIALS, e);
        }
    }

    @MediaType("application/json")
    @Throws(ExecuteErrorsProvider.class)
    public InputStream postMessage(@Config BasicConfiguration configuration, @Connection BasicConnection connection, @NotNull String channel, String text) {
        try {

            JSONObject obj = new JSONObject();

            obj.put("channel",channel);
            obj.put("text", text);

            String jsonString = obj.toString();

            HttpResponse response = connection.doRequest(POST_MESSAGE, BASE_URI, HttpConstants.Method.POST, jsonString, "");

            String result = convertStreamToString(response);

            boolean okResult = evalauteExpression(response.getEntity().getContent(),"#[payload.ok]");

            if(okResult)
            {
                return response.getEntity().getContent();
            }
            throw new IllegalStateException();
        } catch (IllegalStateException e) {
            throw new ModuleException(SlackErrors.INVALID_PARAMETER, e);
        }
    }

    @MediaType("application/json")
    @Throws(ExecuteErrorsProvider.class)
    public InputStream getChannelInfo(@Config BasicConfiguration configuration, @Connection BasicConnection connection, @NotNull String channel) {
        try {

            String url="?channel=" + channel;

            HttpResponse response = connection.doRequest(CONVERSATIONS_INFO, BASE_URI, HttpConstants.Method.GET, "", url);

            boolean okResult = evalauteExpression(response.getEntity().getContent(),"#[payload.ok]");

            if(okResult)
            {
                return response.getEntity().getContent();
            }
            throw new IllegalStateException();
        } catch (IllegalStateException e) {
            throw new ModuleException(SlackErrors.INVALID_PARAMETER, e);
        }
    }

    private boolean evalauteExpression(InputStream inputStream, String expression)
    {
        TypedValue<String> tvInput = new TypedValue(inputStream, DataType.JSON_STRING);

        TypedValue<?> typedValue = expressionLanguage
            .evaluate(
                expression,
                BindingContext.builder()
                    .addBinding("payload", tvInput)
                    .build());

        return Boolean.parseBoolean(typedValue.getValue().toString());
    }

}
