package de.adorsys.ledgers.oba.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentAuthorisationResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.rest.api.exception.PaymentAuthorisationException;
import de.adorsys.ledgers.oba.rest.server.resource.utils.AuthUtils;
import de.adorsys.ledgers.oba.rest.server.resource.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Service
public class LoginService {

    private final UserMgmtRestClient userManagementRestClient;
    private final ResponseUtils responseUtils;
    private final CommonPisService commonPisService;

    public ResponseEntity<PaymentAuthorisationResponse> login(String encryptedPaymentId,
                                                              String authorisationId,
                                                              String login,
                                                              String pin,
                                                              String consentCookieString,
                                                              HttpServletResponse response) {

        PaymentWorkflow workflow;
        try {
            workflow = commonPisService.identifyPayment(encryptedPaymentId, authorisationId, false, consentCookieString, login, response, null);
        } catch (PaymentAuthorisationException e) {
            return e.getError();
        }

        // Authorise
        ResponseEntity<SCALoginResponseTO> authoriseForConsent =
            userManagementRestClient.authoriseForConsent(login, pin, workflow.getPaymentId(), workflow.getAuthorisationId(), OpTypeTO.PAYMENT);
        commonPisService.processSCAResponse(workflow, authoriseForConsent.getBody());

        boolean success = AuthUtils.success(authoriseForConsent);

        if (success) {
            String psuId = AuthUtils.psuId(workflow.getBearerToken());
            try {
                commonPisService.scaStatus(workflow, psuId, response);
                commonPisService.initiatePayment(workflow, response);

                // Select sca if no alternative.
                if (workflow.isSingleScaMethod()) {
                    ScaUserDataTO scaUserDataTO = workflow.getScaMethods().iterator().next();
                    commonPisService.selectMethod(scaUserDataTO.getId(), workflow);
                }

                commonPisService.updateScaStatusPaymentStatusConsentData(psuId, workflow, response);
            } catch (PaymentAuthorisationException e) {
                return e.getError();
            }

            switch (workflow.getScaStatus()) {
                case PSUIDENTIFIED:
                case FINALISED:
                case EXEMPTED:
                case PSUAUTHENTICATED:
                case SCAMETHODSELECTED:
                    responseUtils.setCookies(response, workflow.getConsentReference(), workflow.getBearerToken().getAccess_token(), workflow.getBearerToken().getAccessTokenObject());
                    return ResponseEntity.ok(workflow.getAuthResponse());
                case STARTED:
                case FAILED:
                default:
                    // failed Message. No repeat. Delete cookies.
                    responseUtils.removeCookies(response);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            // failed Message. No repeat. Delete cookies.
            responseUtils.removeCookies(response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
