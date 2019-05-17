package de.adorsys.ledgers.oba.rest.server.config.cors;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "server.servlet.session.cookie")
public class CookieConfigProperties {
    private boolean secure;
    private int maxAge;
    private boolean httpOnly;
    private String path;
}
