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

import static it.jwisniowski.poc.springboot.http100continue.DemoController.Action.*;
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
    void reads_body_returns_no_body(CapturedOutput output) throws IOException, InterruptedException {
        String expectedBodyContent = "some body content";

        HttpResponse<String> response = httpClient.send(
                continueRequestBuilder()
                        .setHeader("Action", READ_BODY_RETURN_NO_BODY.name())
                        .POST(BodyPublishers.ofString(expectedBodyContent))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertThat(response)
                .extracting(HttpResponse::statusCode)
                .isEqualTo(201);

        assertThat(output)
                .contains(expectedBodyContent);
    }

    @Test
    void errors_does_not_read_body() throws IOException, InterruptedException {
        String expectedBodyContent = "some body content";

        HttpResponse<String> response = httpClient.send(
                continueRequestBuilder()
                        .setHeader("Action", ERROR_DON_NOT_READ_BODY.name())
                        .POST(BodyPublishers.ofString(expectedBodyContent))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertThat(response)
                .extracting(HttpResponse::statusCode)
                .isEqualTo(400);
    }

    @Test
    void errors_after_starting_to_read_body() throws IOException, InterruptedException {
        String expectedBodyContent = "some body content";

        HttpResponse<String> response = httpClient.send(
                continueRequestBuilder()
                        .setHeader("Action", ERROR_AFTER_STARTING_TO_READ_BODY.name())
                        .POST(BodyPublishers.ofString(expectedBodyContent))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertThat(response)
                .extracting(HttpResponse::statusCode)
                .isEqualTo(400);
    }

    private HttpRequest.Builder continueRequestBuilder() {
        return HttpRequest.newBuilder()
                .expectContinue(true)
                .uri(serverUri.resolve("demo-resource"));
    }
}
