package de.adorsys.ledgers.oba.rest.server.resource.redirect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.rest.api.resource.redirect.RedirectApi;
import de.adorsys.ledgers.oba.rest.server.auth.ObaMiddlewareAuthentication;
import de.adorsys.ledgers.oba.rest.server.resource.AuthUtils;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.domain.redirect.RedirectContext;
import de.adorsys.ledgers.oba.service.api.domain.redirect.RequestType;
import de.adorsys.ledgers.oba.service.api.service.redirect.CmsRedirectService;
import de.adorsys.ledgers.oba.service.api.service.redirect.RedirectCommonService;
import de.adorsys.psd2.consent.api.ais.CmsAisAccountConsent;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.adorsys.ledgers.oba.service.api.domain.redirect.RequestType.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(RedirectApi.BASE_PATH)
@Api(value = RedirectApi.BASE_PATH, tags = "PSU REDIRECT CONTROLLERS. Provides access to online banking redirect functionality")
public class RedirectController implements RedirectApi {
    private final RedirectCommonService commonService;
    private final ObaMiddlewareAuthentication middlewareAuth;
    private final CmsRedirectService cmsRedirectService;
    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<RedirectContext> loginForOperation(String encryptedOperationId, String authorisationId, OpTypeTO opType, String login, String pin) {
        RedirectContext startContext = getInitialContext(LOGIN, encryptedOperationId, authorisationId, opType, null, login);
        RedirectContext response = commonService.loginForOperation(startContext, login, pin);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<RedirectContext> init(String encryptedOperationId, String authorisationId, OpTypeTO opType, Object operationObject) {
        RedirectContext startContext = getInitialContext(INIT, encryptedOperationId, authorisationId, opType, mapObject(opType, operationObject), AuthUtils.psuId(middlewareAuth));
        RedirectContext response = commonService.initOperation(startContext);
        return ResponseEntity.ok(response);
    }

    private Object mapObject(OpTypeTO opType, Object operationObject) {
        try {
            String valueAsString = objectMapper.writeValueAsString(operationObject);
            Class<?> aClass = opType == OpTypeTO.CONSENT
                                  ? CmsAisAccountConsent.class
                                  : PaymentTO.class;
            return objectMapper.readValue(valueAsString, aClass);
        } catch (JsonProcessingException e) {
            throw new ObaException("Could not Deserialize Operation Object!", ObaErrorCode.CONVERSION_EXCEPTION);
        }
    }

    @Override
    public ResponseEntity<RedirectContext> selectMethod(String encryptedOperationId, String authorisationId, OpTypeTO opType, String scaMethodId) {
        RedirectContext startContext = getInitialContext(SELECT_SCA, encryptedOperationId, authorisationId, opType, scaMethodId, AuthUtils.psuId(middlewareAuth));
        RedirectContext response = commonService.selectMethod(startContext);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<RedirectContext> authorizeOperation(String encryptedOperationId, String authorisationId, OpTypeTO opType, String authCode) {
        RedirectContext startContext = getInitialContext(VALIDATE, encryptedOperationId, authorisationId, opType, authCode, AuthUtils.psuId(middlewareAuth));
        RedirectContext response = commonService.authorizeOperation(startContext);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<RedirectContext> revoke(String encryptedOperationId, String authorisationId, OpTypeTO opType) {
        RedirectContext startContext = getInitialContext(FAIL, encryptedOperationId, authorisationId, opType, null, AuthUtils.psuId(middlewareAuth));
        RedirectContext response = commonService.failOperation(startContext);
        return ResponseEntity.ok(response);
    }

    private RedirectContext getInitialContext(RequestType requestType, String encryptedOperationId, String authorisationId, OpTypeTO opType, Object operationObject, String login) {
        BearerTokenTO bearerToken = requestType == LOGIN
                                        ? null
                                        : middlewareAuth.getBearerToken();
        RedirectContext context = new RedirectContext(requestType, encryptedOperationId, authorisationId, opType, operationObject, login, bearerToken);
        cmsRedirectService.getCmsObject(context);
        return context;
    }
}
