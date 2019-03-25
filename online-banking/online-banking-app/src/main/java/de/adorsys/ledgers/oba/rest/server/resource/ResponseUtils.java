package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.oba.rest.api.domain.OnlineBankingResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PsuMessage;
import de.adorsys.ledgers.oba.rest.api.domain.PsuMessageCategory;
import de.adorsys.ledgers.oba.rest.api.domain.ValidationCode;
import de.adorsys.ledgers.oba.rest.server.auth.MiddlewareAuthentication;
import de.adorsys.ledgers.oba.rest.server.service.CookieService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class ResponseUtils {
    private static final String LOCATION_HEADER_NAME = "Location";
    private static final String TOKEN_PREFIX = "Bearer ";

    private static final String UNKNOWN_CREDENTIALS = "Unknown credentials";
    private static final String REQUEST_WITH_REDIRECT_NOT_FOUND = "Request with redirect id not found";

    private final CookieService cookieService;

    public <T extends OnlineBankingResponse> ResponseEntity<T> forbidden(T authResp, String message, HttpServletResponse httpResp) {
        return error(authResp, HttpStatus.FORBIDDEN, message, httpResp);
    }

    public <T extends OnlineBankingResponse> ResponseEntity<T> unknownCredentials(
        T resp, HttpServletResponse httpResp) {
        return error(resp, HttpStatus.FORBIDDEN, UNKNOWN_CREDENTIALS, httpResp);
    }

    public <T extends OnlineBankingResponse> ResponseEntity<T> requestWithRedNotFound(T authResp, HttpServletResponse httpResp) {
        return error(authResp, HttpStatus.NOT_FOUND, REQUEST_WITH_REDIRECT_NOT_FOUND, httpResp);
    }

    public <T extends OnlineBankingResponse> ResponseEntity<T> couldNotProcessRequest(T authResp,
                                                                                      HttpStatus status, HttpServletResponse httpResp) {
        return couldNotProcessRequest(authResp, "Could not process request. See status code.", status, httpResp);
    }

    public <T extends OnlineBankingResponse> ResponseEntity<T> couldNotProcessRequest(T authResp, String message,
                                                                                      HttpStatus status, HttpServletResponse httpResp) {
        return error(authResp, status, message, httpResp);
    }

    public String authHeader(MiddlewareAuthentication auth) {
        return TOKEN_PREFIX + auth.getBearerToken().getAccess_token();
    }

    public <T extends OnlineBankingResponse> ResponseEntity<T> redirect(String locationURI, HttpServletResponse httpResp) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(LOCATION_HEADER_NAME, locationURI);
        cookieService.removeCookies(httpResp);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    public <T extends OnlineBankingResponse> ResponseEntity<T> redirectKeepCookie(String locationURI, HttpServletResponse httpResp) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(LOCATION_HEADER_NAME, locationURI);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    public <T extends OnlineBankingResponse> ResponseEntity<T> backToSender(T authResp, String tppNokRedirectUri, String tppOkRedirectUri,
                                                                            HttpServletResponse httpResp, HttpStatus originalStatus, ValidationCode validationCode) {
        String locationUri = StringUtils.isNotBlank(tppNokRedirectUri)
                                 ? tppNokRedirectUri
                                 : tppOkRedirectUri;
        if (StringUtils.isBlank(locationUri)) {
            return couldNotProcessRequest(authResp, "Missing tpp redirect uri.", HttpStatus.BAD_REQUEST, httpResp);
        }
        String uriString = UriComponentsBuilder.fromUriString(locationUri).queryParam("VALIDATION_CODE", validationCode.name()).build().toUriString();
        return redirect(uriString, httpResp);
    }

    public <T extends OnlineBankingResponse> ResponseEntity<T> error(T authResp, HttpStatus status, String message, HttpServletResponse response) {
        PsuMessage psuMessage = new PsuMessage();
        psuMessage.setCategory(PsuMessageCategory.ERROR);
        psuMessage.setText(message);
        psuMessage.setCode(status.toString());
        authResp.getPsuMessages().add(psuMessage);
        cookieService.removeCookies(response);
        return ResponseEntity.status(status).body(authResp);
    }

    public <T extends OnlineBankingResponse> ResponseEntity<T> badRequest(T authResp, String message, HttpServletResponse httpResp) {
        return error(authResp, HttpStatus.BAD_REQUEST, message, httpResp);
    }
}
