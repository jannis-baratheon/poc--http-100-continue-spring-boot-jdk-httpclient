package it.jwisniowski.poc.springboot.http100continue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("api")
public class DemoController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @PostMapping(path = "continue-and-read-fully-no-response-body")
    public ResponseEntity<?> continueAndReadFullyNoResponseBody(
            @RequestHeader("Demo-Header") String demoHeaderValue,
            HttpServletRequest httpServletRequest) throws IOException {
        log.info("Received a header. Header value is: {}", demoHeaderValue);
        log.info("Reading the request body");

        try (ServletInputStream sis = httpServletRequest.getInputStream()) {
            log.info("Request body is: {}", new String(sis.readAllBytes()));
        }

        log.info("Returning response");

        return ResponseEntity.created(URI.create("/entity/1")).build();
    }
}
