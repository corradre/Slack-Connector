package org.mule.extension.internal;

import org.mule.runtime.extension.api.annotation.param.RefName;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.HttpService;
import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.client.HttpClientConfiguration;
import org.mule.runtime.http.api.client.proxy.ProxyConfig;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import static java.lang.String.format;

/**
 * This class represents an extension connection just as example (there is no real connection with anything here c:).
 */
public final class BasicConnection {

  private String token;

  private HttpClient httpClient;

  // si no ponia esto rompia.
  static final String CLIENT_NAME_PATTERN = "rest.connector.%s";

  /**
   * The name of the config in which this connection is defined
   */
  @RefName
  private String configName;

  @Inject
  private HttpService httpService;

  public BasicConnection(String token, HttpService httpService) {
    this.token = token;
    initHttpClient(httpService,null);
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }

  public void stop()
  {
    this.httpClient.stop();
  }


  public HttpResponse doRequest(String endpoint, String base_uri, HttpConstants.Method method) {

    HttpRequest request;
    request = HttpRequest.builder()
        .uri(URI.create(base_uri + endpoint))
        .addHeader("Authorization", "Bearer " + token)
        .addHeader("Content-Type", "application/json")
        .method(method)
        .build();

    try {
      HttpResponse response=httpClient.send(request, 1000, true, null);
      return response;
    }catch(Exception e){
      return null;
    }
  }

  private void initHttpClient(HttpService httpService, ProxyConfig proxyConfig) {
    HttpClientConfiguration.Builder builder = new HttpClientConfiguration.Builder()
        .setName(format(CLIENT_NAME_PATTERN, configName));
    if (proxyConfig != null) {
      builder.setProxyConfig(proxyConfig);
    }
    httpClient = httpService.getClientFactory().create(builder.build());
    httpClient.start();
  }

  public static String convertStreamToString(HttpResponse response) {

    HttpEntity entity = response.getEntity();
    InputStream instream = entity.getContent();
    BufferedReader bReader = new BufferedReader(new InputStreamReader(instream));
    StringBuilder stringBuilder = new StringBuilder();

    String line = null;
    try {
      while ((line = bReader.readLine()) != null) {
        stringBuilder.append(line + "\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return stringBuilder.toString();
  }
}
