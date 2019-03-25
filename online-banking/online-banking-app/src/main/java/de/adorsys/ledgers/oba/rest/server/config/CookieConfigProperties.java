package de.adorsys.ledgers.oba.rest.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "server.servlet.session.cookie")
public class CookieConfigProperties {
    private String domain;
    private boolean httpOnly;
    private int maxAge;
    private String path;
    private boolean secure;
}
