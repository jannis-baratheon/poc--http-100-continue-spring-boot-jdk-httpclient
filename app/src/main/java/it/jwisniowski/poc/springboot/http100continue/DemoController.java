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
    public enum Action {
        READ_BODY_RETURN_NO_BODY
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    @PostMapping(path = "demo-resource")
    public ResponseEntity<?> continueAndReadFullyNoResponseBody(
            @RequestHeader("Action") Action action,
            HttpServletRequest httpServletRequest) throws IOException {
        log.info("Received a header. Header value is: {}", action);

        if (action == Action.READ_BODY_RETURN_NO_BODY) {
            log.info("Reading the request body");

            try (ServletInputStream sis = httpServletRequest.getInputStream()) {
                log.info("Request body is: {}", new String(sis.readAllBytes()));
            }

            log.info("Returning response");

            return ResponseEntity.created(URI.create("/entity/1")).build();
        }

        throw new UnsupportedOperationException();
    }
}
