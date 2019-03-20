package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentType;
import de.adorsys.ledgers.oba.rest.api.domain.AuthorisationResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentAuthorisationResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.rest.api.exception.PaymentAuthorisationException;
import de.adorsys.ledgers.oba.rest.api.resource.PISApi;
import de.adorsys.ledgers.oba.rest.server.resource.utils.AuthUtils;
import de.adorsys.ledgers.oba.rest.server.service.CommonPisService;
import de.adorsys.ledgers.oba.rest.server.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController(PisController.BASE_PATH)
@RequestMapping(PisController.BASE_PATH)
@Api(value = PisController.BASE_PATH, tags = "PSU PIS", description = "Provides access to online banking payment functionality")
public class PisController extends AbstractXisController implements PISApi {

    private final PaymentRestClient paymentRestClient;
    private final LoginService loginService;
    private final CommonPisService commonPisService;

    @Override
    public ResponseEntity<AuthorisationResponse> preparePaymentForAuthorisation(String redirectId, String encryptedPaymentId) {
        return auth(redirectId, ConsentType.PIS, encryptedPaymentId, request, response);
    }

    @Override
    @ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
    public ResponseEntity<PaymentAuthorisationResponse> login(String encryptedPaymentId,
                                                              String authorisationId,
                                                              String login,
                                                              String pin,
                                                              String consentCookieString) {
        return loginService.login(encryptedPaymentId, authorisationId, login, pin, consentCookieString, response);
    }

    @Override
    public ResponseEntity<PaymentAuthorisationResponse> initiatePayment(
        String encryptedPaymentId, String authorisationId, String consentAndAccessTokenCookieString) {

        try {
            String psuId = AuthUtils.getPsuId(auth);
            // Identity the link and load the workflow.
            PaymentWorkflow workflow = commonPisService.identifyPayment(encryptedPaymentId, authorisationId, true, consentAndAccessTokenCookieString, psuId, response, auth.getBearerToken());

            // Update status
            workflow.getScaResponse().setScaStatus(ScaStatusTO.PSUAUTHENTICATED);
            commonPisService.scaStatus(workflow, psuId, response);

            commonPisService.initiatePayment(workflow, response);
            commonPisService.updateScaStatusPaymentStatusConsentData(psuId, workflow, response);

            // Store result in token.
            responseUtils.setCookies(response, workflow.getConsentReference(), workflow.getBearerToken().getAccess_token(), workflow.getBearerToken().getAccessTokenObject());
            return ResponseEntity.ok(workflow.getAuthResponse());
        } catch (PaymentAuthorisationException e) {
            return e.getError();
        }
    }

    @Override
    public ResponseEntity<PaymentAuthorisationResponse> selectScaMethod(
        String encryptedPaymentId, String authorisationId,
        String scaMethodId, String consentAndaccessTokenCookieString) {

        String psuId = AuthUtils.getPsuId(auth);
        try {
            PaymentWorkflow workflow = commonPisService.identifyPayment(encryptedPaymentId, authorisationId, true, consentAndaccessTokenCookieString, psuId, response, auth.getBearerToken());
            commonPisService.selectMethod(scaMethodId, workflow);

            commonPisService.updateScaStatusPaymentStatusConsentData(psuId, workflow, response);

            responseUtils.setCookies(response, workflow.getConsentReference(), workflow.getBearerToken().getAccess_token(), workflow.getBearerToken().getAccessTokenObject());
            return ResponseEntity.ok(workflow.getAuthResponse());
        } catch (PaymentAuthorisationException e) {
            return e.getError();
        }
    }

    @Override
    public ResponseEntity<PaymentAuthorisationResponse> authorisePayment(
        String encryptedPaymentId,
        String authorisationId,
        String consentAndaccessTokenCookieString, String authCode) {

        String psuId = AuthUtils.getPsuId(auth);
        try {
            PaymentWorkflow workflow = commonPisService.identifyPayment(encryptedPaymentId, authorisationId, true, consentAndaccessTokenCookieString, psuId, response, auth.getBearerToken());

            authInterceptor.setAccessToken(workflow.getBearerToken().getAccess_token());

            SCAPaymentResponseTO scaPaymentResponse = paymentRestClient.authorizePayment(workflow.getPaymentId(), workflow.getAuthorisationId(), authCode).getBody();
            commonPisService.processPaymentResponse(workflow, scaPaymentResponse);

            commonPisService.updateScaStatusPaymentStatusConsentData(psuId, workflow, response);

            responseUtils.setCookies(response, workflow.getConsentReference(), workflow.getBearerToken().getAccess_token(), workflow.getBearerToken().getAccessTokenObject());
            return ResponseEntity.ok(workflow.getAuthResponse());
        } catch (PaymentAuthorisationException e) {
            return e.getError();
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    @Override
    public String getBasePath() {
        return BASE_PATH;
    }

}
