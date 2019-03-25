package de.adorsys.ledgers.oba.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.HttpCookie;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static de.adorsys.ledgers.oba.rest.server.resource.CookieName.ACCESS_TOKEN_COOKIE_NAME;
import static de.adorsys.ledgers.oba.rest.server.resource.CookieName.CONSENT_COOKIE_NAME;

@Service
public class CookieService {
    @Value("${online.banking.https.enabled:false}")
    private boolean httpsEnabled;

    public String readCookie(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        return cookie != null
                   ? cookie.getValue()
                   : null;
    }

    public Cookie createCookie(String name, String value, int validity) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(httpsEnabled);
        cookie.setMaxAge(validity);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        return cookie;
    }

    public String parseCookie(String cookieString, String name) {
        List<HttpCookie> cookies = HttpCookie.parse(cookieString);
        for (HttpCookie httpCookie : cookies) {
            if (StringUtils.equalsIgnoreCase(httpCookie.getName(), name)) {
                return httpCookie.getValue();
            }
        }
        return null;
    }

    /*
     * Set both access token cookie and consent cookie.
     *
     * @param response
     *
     * @param consentReference
     *
     * @param accessTokenString
     *
     * @param accessTokenTO
     */
    public void setCookies(HttpServletResponse response, ConsentReference consentReference, String accessTokenString,
                           AccessTokenTO accessTokenTO) {

        int validity = 300;// default to five seconds.
        if (StringUtils.isNoneBlank(accessTokenString) && accessTokenTO != null) {
            long diffInMillies = Math.abs(new Date().getTime() - accessTokenTO.getExp().getTime());
            validity = ((Long) TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS)).intValue();
            // Set Cookie. Access Token
            Cookie accessTokenCookie = createCookie(ACCESS_TOKEN_COOKIE_NAME, accessTokenString, validity);
            response.addCookie(accessTokenCookie);
        } else {
            removeCookie(response, ACCESS_TOKEN_COOKIE_NAME);
        }

        if (consentReference != null && StringUtils.isNoneBlank(consentReference.getCookieString())) {
            // Set cookie consent
            Cookie consentCookie = createCookie(CONSENT_COOKIE_NAME, consentReference.getCookieString(), validity);
            response.addCookie(consentCookie);
        }
    }

    public void removeCookies(HttpServletResponse response) {
        removeCookie(response, ACCESS_TOKEN_COOKIE_NAME);
        removeCookie(response, CONSENT_COOKIE_NAME);
    }

    private void removeCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(httpsEnabled);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
