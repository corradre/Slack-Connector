package org.mule.extension.internal;

import static org.mule.extension.internal.BasicConnection.convertStreamToString;
import static org.mule.extension.internal.SlackEndPoints.*;

import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import javax.validation.constraints.NotNull;
import java.io.InputStream;
public class BasicOperations {

    @MediaType("application/json")
    @Throws(ExecuteErrorsProvider.class)
    public InputStream getChannels(@Config BasicConfiguration configuration, @Connection BasicConnection connection) {
        try {
            HttpResponse response = connection.doRequest(CONVERSATION_LIST, BASE_URI, HttpConstants.Method.GET, null);
            String result = null;
            result = convertStreamToString(response);
            if (result.contains("\"ok\":false,\"error\":\"invalid_auth\"")) {
                throw new IllegalStateException();
            } else {
                return response.getEntity().getContent();
            }
        } catch (IllegalStateException e) {
            throw new ModuleException(SlackErrors.BAD_CREDENTIALS, e);
        }
    }

    @MediaType("application/json")
    @Throws(ExecuteErrorsProvider.class)
    public InputStream postMessage(@Config BasicConfiguration configuration, @Connection BasicConnection connection, @NotNull String channel, String text) {
        try {

            String body = "{\n" +
                "  \"channel\": \""+channel+"\",\n" +
                "  \"text\": \" "+ text+" \"\n" +
                "}";

            HttpResponse response = connection.doRequest(POST_MESSAGE, BASE_URI, HttpConstants.Method.POST, body);

            String result = null;
            result = convertStreamToString(response);

            if (result.contains("\"ok\":false,\"error\":\"channel_not_found\"")) {
                throw new IllegalStateException();
            } else {
                return response.getEntity().getContent();
            }
        } catch (IllegalStateException e) {
            throw new ModuleException(SlackErrors.INVALID_PARAMETER, e);
        }
    }
}
