package org.mule.extension.internal;

import org.json.JSONObject;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.el.BindingContext;
import org.mule.runtime.api.el.ExpressionLanguage;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Export;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.HttpService;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import javax.inject.Inject;
import java.io.InputStream;

import static org.mule.extension.internal.BasicConnection.convertStreamToString;
import static org.mule.extension.internal.SlackEndPoints.AUTH_TEST;
import static org.mule.extension.internal.SlackEndPoints.BASE_URI;


/**
 * This class (as it's name implies) provides connection instances and the funcionality to disconnect and validate those
 * connections.
 * <p>
 * All connection related parameters (values required in order to create a connection) must be
 * declared in the connection providers.
 * <p>
 * This particular example is a {@link PoolingConnectionProvider} which declares that connections resolved by this provider
 * will be pooled and reused. There are other implementations like {@link CachedConnectionProvider} which lazily creates and
 * caches connections or simply {@link ConnectionProvider} if you want a new connection each time something requires one.
 */
public class BasicConnectionProvider implements PoolingConnectionProvider<BasicConnection> {

    @DisplayName("Token")
    @Parameter
    private String token;

    @Inject
    private HttpService httpService;

    @Inject
    ExpressionLanguage expressionLanguage;

  @Override
  public BasicConnection connect() throws ConnectionException {
    return new BasicConnection(token, httpService);
  }

    @Override public void disconnect(BasicConnection basicConnection) {
    }

    @Override
    public ConnectionValidationResult validate(BasicConnection connection) {
        try {
            HttpResponse response = connection.doRequest(AUTH_TEST, BASE_URI, HttpConstants.Method.POST, "");
            InputStream content = response.getEntity().getContent();

            TypedValue<String> tvInput = new TypedValue(content, DataType.JSON_STRING);

            TypedValue<?> typedValue = expressionLanguage
                .evaluate(
                    "#[payload.ok]",
                    BindingContext.builder()
                        .addBinding("payload", tvInput)
                        .build());

            boolean okResult = Boolean.parseBoolean(typedValue.getValue().toString());

            if(okResult)
            {
                return ConnectionValidationResult.success();
            }
        }
        catch (Throwable t)
        {
            String asd = "asd";
        }
        return ConnectionValidationResult.failure("fallo", null);
    }
}
