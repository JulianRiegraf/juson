package jira;


import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;


/* HTTP GET Request, Response (Java 11 APIs)  */
public class JiraCaller {

  private final String API_ENDPOINT = "http://api.nytimes.com/svc/topstories/v2/world.json";
  private final String API_ENDPOINT_KEY = "f542cfcbda0c4488aed25f5ebf0dbad2";
  private final HttpResponse.BodyHandler<String> asString = HttpResponse.BodyHandlers.ofString();

  private final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
      .followRedirects(HttpClient.Redirect.NORMAL).proxy(ProxySelector.getDefault()).build();


  public Optional<String> call() {
    // HTTP GET REQUEST
    var HTTP_REQUEST = HttpRequest.newBuilder()
        .uri(URI.create( //Set the appropriate endpoint
            new StringBuilder(API_ENDPOINT)
                .append("?api-key=").append(API_ENDPOINT_KEY)
                .toString()))
        .timeout(Duration.ofMinutes(1))
        .header("Content-Type", "application/json")
        .build();

    // SEND HTTP GET REQUEST, RECIEVE OBJECT FOR HTTP GET RESPONSE
    HttpResponse<String> HTTP_RESPONSE = null;
    try {
      HTTP_RESPONSE = HTTP_CLIENT.send(HTTP_REQUEST, asString);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // HTTP STATUS CODE
    int statusCode = HTTP_RESPONSE.statusCode();

    // HANDLE RESPONSE
    if (statusCode == 200 || statusCode == 201) {
      return Optional.of(HTTP_RESPONSE.body());
    }
    return Optional.empty();
  }
}
