package it.jwisniowski.poc.springboot.http100continue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(OutputCaptureExtension.class)
class DemoApplicationTest {
    @LocalServerPort
    int localServerPort;

    private HttpClient httpClient;
    private URI serverUri;

    @BeforeEach
    void setupClient() {
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        serverUri = URI.create("http://localhost:" + localServerPort + "/api/");
    }

    @Test
    void works_when_body_is_read_fully(CapturedOutput output) throws IOException, InterruptedException {
        String expectedHeaderValue = "some header value";
        String expectedBodyContent = "some body content";

        HttpResponse<String> response = httpClient.send(
                HttpRequest.newBuilder()
                        .expectContinue(true)
                        .setHeader("Demo-Header", expectedHeaderValue)
                        .uri(getUri("continue-and-read-fully-no-response-body"))
                        .POST(BodyPublishers.ofString(expectedBodyContent))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertThat(response)
                .extracting(HttpResponse::statusCode, HttpResponse::body)
                .containsExactly(201, "");

        assertThat(output.getOut())
                .contains(expectedHeaderValue)
                .contains(expectedBodyContent);
    }

    private URI getUri(String endpoint) {
        return serverUri.resolve(endpoint);
    }
}
