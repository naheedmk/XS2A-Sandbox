package de.adorsys.ledgers.oba.rest.server.auth;


import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.rest.server.resource.CookieName;
import de.adorsys.ledgers.oba.rest.server.service.CookieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenAuthenticationService {
    private final UserMgmtRestClient ledgersUserMgmt;
    private final AuthRequestInterceptor authInterceptor;
    private final CookieService cookieService;

    public Authentication getAuthentication(HttpServletRequest request) {
        String accessToken = cookieService.readCookie(request, CookieName.ACCESS_TOKEN_COOKIE_NAME);

        if (StringUtils.isBlank(accessToken)) {
            debug(String.format("Missing cookie with name %s.", CookieName.ACCESS_TOKEN_COOKIE_NAME));
            return null;
        }

        BearerTokenTO bearerToken = null;
        try {
            authInterceptor.setAccessToken(accessToken);
            ResponseEntity<BearerTokenTO> responseEntity = ledgersUserMgmt.validate(accessToken);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                bearerToken = responseEntity.getBody();
            }
        } finally {
            authInterceptor.setAccessToken(null);
        }

        if (bearerToken == null) {
            debug("Token is not valid.");
            return null;
        }

        // process roles
        AccessTokenTO token = bearerToken.getAccessTokenObject();

        return new MiddlewareAuthentication(token.getSub(), bearerToken, getGrantedAuthorities(token));
    }

    private List<GrantedAuthority> getGrantedAuthorities(AccessTokenTO token) {
        return token.getRole() != null
                   ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + token.getRole().name()))
                   : Collections.emptyList();
    }

    private void debug(String s) {
        if (log.isDebugEnabled()) {
            log.debug(s);
        }
    }
}
