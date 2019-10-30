package de.adorsys.ledgers.oba.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.oauth.OauthServerInfoTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OauthServerLinkResolver {
    private OauthServerInfoTO info;
    private Map<String, String> objectIds = new HashMap<>();
    private Map<String, String> pathPart = new HashMap<>();
    private String redirectId;
    private Optional<String> requestParameter;

    @Value("${oba.url}")
    private String OBA_FE_BASE_URI = "http://localhost:4400";
    @Value("${self.url}")
    private String OBA_BE_BASE_URI = "http://localhost:4400";

    public OauthServerLinkResolver(OauthServerInfoTO info, String paymentId, String consentId, String cancellationId, String redirectId) {
        this.info = info;
        objectIds.put("paymentId", paymentId);
        objectIds.put("encryptedConsentId", consentId);
        objectIds.put("cancellationId", cancellationId);

        this.requestParameter = resolvePresentParameter();

        pathPart.put("paymentId", "payment-initiation");
        pathPart.put("encryptedConsentId", "account-information");
        pathPart.put("cancellationId", "payment-cancellation");
        this.redirectId = redirectId;
    }

    public OauthServerInfoTO resolve() {
        String authUri = StringUtils.isNotBlank(redirectId) && requestParameter.isPresent()
                             ? resolveParametrizedAuthUri()
                             : resolveNonParametrizedAuthUri();
        info.setAuthorizationEndpoint(authUri);
        info.setTokenEndpoint(resolveTokenUri());
        return info;
    }

    private String resolveParametrizedAuthUri() {
        String param = requestParameter.get();
        return UriComponentsBuilder
                   .fromUriString(OBA_FE_BASE_URI)
                   .pathSegment(pathPart.get(param))
                   .pathSegment("login")
                   .queryParam("redirectId", redirectId)
                   .queryParam(param, objectIds.get(param))
                   .queryParam("oauth2", true)
                   .build()
                   .toUriString();
    }

    private String resolveNonParametrizedAuthUri() {
        return UriComponentsBuilder
                   .fromUriString(OBA_FE_BASE_URI)
                   .pathSegment("auth/authorize")
                   .queryParam("redirect_uri=")
                   .build()
                   .toUriString();
    }

    private String resolveTokenUri() {
        return UriComponentsBuilder
                   .fromUriString(OBA_BE_BASE_URI)
                   .pathSegment("oauth/token")
                   .build()
                   .toUriString();
    }

    private Optional<String> resolvePresentParameter() {
        return objectIds.entrySet().stream()
                   .filter(e -> StringUtils.isNotBlank(e.getValue()))
                   .map(Map.Entry::getKey)
                   .findFirst();
    }
}
