package de.adorsys.ledgers.oba.rest.server.resource.utils;

import org.springframework.http.ResponseEntity;

import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.rest.server.auth.MiddlewareAuthentication;

public class AuthUtils {

    public static boolean isSuccessfulAuthorisation(ResponseEntity<SCALoginResponseTO> scaLoginResponse) {
        return scaLoginResponse != null && scaLoginResponse.getBody() != null && scaLoginResponse.getBody().getBearerToken() != null;
    }

    public static String getPsuId(MiddlewareAuthentication authentication) {
        if (authentication == null) {
            return null;
        }
        return getPsuId(authentication.getBearerToken());
    }

    public static String getPsuId(BearerTokenTO bearerToken) {
        return bearerToken.getAccessTokenObject().getLogin();
    }

}
