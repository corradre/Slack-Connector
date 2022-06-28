package org.mule.extension.internal;

import static org.mule.extension.internal.BasicConnection.convertStreamToString;
import static org.mule.extension.internal.SlackEndPoints.BASE_URI;
import static org.mule.extension.internal.SlackEndPoints.CONVERSATION_LIST;

import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import java.io.InputStream;
public class BasicOperations {

    @MediaType("application/json")
    @Throws(ExecuteErrorsProvider.class)
    public InputStream getChannels(@Config BasicConfiguration configuration, @Connection BasicConnection connection) {
        try {
            HttpResponse response = connection.doRequest(CONVERSATION_LIST, BASE_URI, HttpConstants.Method.GET);
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
}
