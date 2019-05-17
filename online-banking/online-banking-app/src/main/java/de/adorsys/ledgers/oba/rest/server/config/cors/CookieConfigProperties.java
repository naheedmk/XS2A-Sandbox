package de.adorsys.ledgers.oba.rest.server.config.cors;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "server.servlet.session.cookie")
public class CookieConfigProperties {
    private Boolean secure = false;
    private Integer maxAge = 300;
    private Boolean httpOnly = true;
    private String path = "/";
}
