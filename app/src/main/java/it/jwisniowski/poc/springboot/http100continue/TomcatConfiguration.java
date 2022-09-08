package it.jwisniowski.poc.springboot.http100continue;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfiguration {
    @Bean
    public TomcatConnectorCustomizer connectorCustomizer() {
        return connector -> {
            // Set Tomcat connector to only send a 100 status when response is being read in the controller
            ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setContinueResponseTiming("onRead");
            // or: connector.setProperty("continueResponseTiming", "onRead");
        };
    }
}
