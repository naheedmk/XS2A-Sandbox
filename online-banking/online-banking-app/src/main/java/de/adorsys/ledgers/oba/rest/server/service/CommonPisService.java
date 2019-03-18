package de.adorsys.ledgers.oba.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTypeTO;
import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.service.TokenStorageService;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReference;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReferencePolicy;
import de.adorsys.ledgers.oba.rest.api.consentref.InvalidConsentException;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentAuthorisationResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.rest.api.domain.ValidationCode;
import de.adorsys.ledgers.oba.rest.api.exception.PaymentAuthorisationException;
import de.adorsys.ledgers.oba.rest.server.mapper.BulkPaymentMapper;
import de.adorsys.ledgers.oba.rest.server.mapper.PeriodicPaymentMapper;
import de.adorsys.ledgers.oba.rest.server.mapper.SinglePaymentMapper;
import de.adorsys.ledgers.oba.rest.server.resource.utils.ResponseUtils;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.pis.CmsBulkPayment;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import de.adorsys.psd2.consent.api.pis.CmsPeriodicPayment;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuPisClient;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Service
public class CommonPisService {

    private final ResponseUtils responseUtils;
    private final ConsentReferencePolicy referencePolicy;
    private final CmsPsuPisClient cmsPsuPisClient;
    private final SinglePaymentMapper singlePaymentMapper;
    private final BulkPaymentMapper bulkPaymentMapper;
    private final PeriodicPaymentMapper periodicPaymentMapper;
    private final PaymentRestClient paymentRestClient;
    private final  AspspConsentDataClient aspspConsentDataClient;
    private final  TokenStorageService tokenStorageService;
    private final  AuthRequestInterceptor authInterceptor;


    @SuppressWarnings("PMD.CyclomaticComplexity")
    public PaymentWorkflow identifyPayment(String encryptedPaymentId, String authorizationId,
                                           boolean strict, String consentCookieString, String psuId, HttpServletResponse
                                                                                                         response, BearerTokenTO bearerToken) throws PaymentAuthorisationException {
        ConsentReference consentReference = null;
        try {
            String consentCookie = responseUtils.consentCookie(consentCookieString);
            consentReference = referencePolicy.fromRequest(encryptedPaymentId, authorizationId, consentCookie, strict);
        } catch (InvalidConsentException e) {
            throw new PaymentAuthorisationException(responseUtils.forbidden(new PaymentAuthorisationResponse(), e.getMessage(), response));
        }

        CmsPaymentResponse cmsPaymentResponse = loadPaymentByRedirectId(psuId, consentReference, response);

        PaymentWorkflow workflow = new PaymentWorkflow(cmsPaymentResponse, consentReference);
        Object convertedPaymentTO = convertPayment(response, workflow.getPaymentType(), cmsPaymentResponse);
        workflow.setAuthResponse(new PaymentAuthorisationResponse(workflow.getPaymentType(), convertedPaymentTO));
        workflow.getAuthResponse().setAuthorisationId(cmsPaymentResponse.getAuthorisationId());
        workflow.getAuthResponse().setEncryptedConsentId(encryptedPaymentId);
        if (bearerToken != null) {
            SCAPaymentResponseTO scaPaymentResponseTO = new SCAPaymentResponseTO();
            scaPaymentResponseTO.setBearerToken(bearerToken);
            workflow.setScaResponse(scaPaymentResponseTO);
        }
        return workflow;
    }

    public void processPaymentResponse(PaymentWorkflow paymentWorkflow, SCAPaymentResponseTO paymentResponse) {
        processSCAResponse(paymentWorkflow, paymentResponse);
        paymentWorkflow.setPaymentStatus(paymentResponse.getTransactionStatus().name());
    }

    public void processSCAResponse(PaymentWorkflow workflow, SCAResponseTO paymentResponse) {
        workflow.setScaResponse(paymentResponse);
        workflow.getAuthResponse().setAuthorisationId(paymentResponse.getAuthorisationId());
        workflow.getAuthResponse().setScaStatus(paymentResponse.getScaStatus());
        workflow.getAuthResponse().setScaMethods(paymentResponse.getScaMethods());
        workflow.setAuthCodeMessage(paymentResponse.getPsuMessage());
    }

    public Object convertPayment(HttpServletResponse response, PaymentTypeTO paymentType,
                                 CmsPaymentResponse paymentResponse) throws PaymentAuthorisationException {
        switch (paymentType) {
            case SINGLE:
                return singlePaymentMapper.toPayment((CmsSinglePayment) paymentResponse.getPayment());
            case BULK:
                return bulkPaymentMapper.toPayment((CmsBulkPayment) paymentResponse.getPayment());
            case PERIODIC:
                return periodicPaymentMapper.toPayment((CmsPeriodicPayment) paymentResponse.getPayment());
            default:
                throw new PaymentAuthorisationException(responseUtils.badRequest(new PaymentAuthorisationResponse(), String.format("Payment type %s not supported.", paymentType.name()), response));
        }
    }

    public void updateScaStatusPaymentStatusConsentData(String psuId, PaymentWorkflow workflow, HttpServletResponse response)
        throws PaymentAuthorisationException {
        // UPDATE CMS
        scaStatus(workflow, psuId, response);
        updatePaymentStatus(response, workflow);
        updateAspspConsentData(workflow, response);
    }

    public void initiatePayment(final PaymentWorkflow paymentWorkflow, HttpServletResponse response) throws PaymentAuthorisationException {
        CmsPaymentResponse paymentResponse = paymentWorkflow.getPaymentResponse();
        Object payment = convertPayment(response, paymentWorkflow.getPaymentType(), paymentResponse);
        try {
            authInterceptor.setAccessToken(paymentWorkflow.getBearerToken().getAccess_token());
            SCAPaymentResponseTO paymentResponseTO = paymentRestClient.initiatePayment(paymentWorkflow.getPaymentType(), payment).getBody();
            processPaymentResponse(paymentWorkflow, paymentResponseTO);
        } catch (FeignException f) {
            paymentWorkflow.setErrorCode(HttpStatus.valueOf(f.status()));
            throw f;
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    public void scaStatus(PaymentWorkflow workflow, String psuId, HttpServletResponse response) throws PaymentAuthorisationException {
        String paymentId = workflow.getPaymentResponse().getPayment().getPaymentId();
        String authorisationId = workflow.getPaymentResponse().getAuthorisationId();
        String status = workflow.getAuthResponse().getScaStatus().name();
        ResponseEntity<Void> resp = cmsPsuPisClient.updateAuthorisationStatus(psuId, null, null, null,
                                                                              paymentId, authorisationId, status, CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID);
        if (!HttpStatus.OK.equals(resp.getStatusCode())) {
            throw new PaymentAuthorisationException(responseUtils.couldNotProcessRequest(new PaymentAuthorisationResponse(), "Error updating authorisation status. See error code.", resp.getStatusCode(), response));
        }
    }

    public void selectMethod(String scaMethodId, final PaymentWorkflow workflow) {
        try {
            authInterceptor.setAccessToken(workflow.getBearerToken().getAccess_token());

            SCAPaymentResponseTO paymentResponseTO = paymentRestClient.selectMethod(workflow.getPaymentId(), workflow.getAuthorisationId(), scaMethodId).getBody();
            processPaymentResponse(workflow, paymentResponseTO);
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private CmsPaymentResponse loadPaymentByRedirectId(String psuId,
                                                       ConsentReference consentReference, HttpServletResponse response) throws PaymentAuthorisationException {
        String psuIdType = null;
        String psuCorporateId = null;
        String psuCorporateIdType = null;
        String redirectId = consentReference.getRedirectId();
        // 4. After user login:
        ResponseEntity<CmsPaymentResponse> responseEntity = cmsPsuPisClient.getPaymentByRedirectId(
            psuId, psuIdType, psuCorporateId, psuCorporateIdType, redirectId, CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID);
        HttpStatus statusCode = responseEntity.getStatusCode();
        if (HttpStatus.OK.equals(statusCode)) {
            return responseEntity.getBody();
        }

        if (HttpStatus.NOT_FOUND.equals(statusCode)) {
            // ---> if(NotFound)
            throw new PaymentAuthorisationException(responseUtils.requestWithRedNotFound(new PaymentAuthorisationResponse(), response));
        }

        if (HttpStatus.REQUEST_TIMEOUT.equals(statusCode)) {
            // ---> if(Expired, TPP-Redirect-URL)
            // 3.a0) LogOut User
            // 3.a1) Send back to TPP
            CmsPaymentResponse payment = responseEntity.getBody();
            String location = StringUtils.isNotBlank(payment.getTppNokRedirectUri())
                                  ? payment.getTppNokRedirectUri()
                                  : payment.getTppOkRedirectUri();
            throw new PaymentAuthorisationException(responseUtils.redirect(location, response));
        } else if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new PaymentAuthorisationException(responseUtils.couldNotProcessRequest(new PaymentAuthorisationResponse(), responseEntity.getStatusCode(), response));
        }

        throw new PaymentAuthorisationException(responseUtils.couldNotProcessRequest(new PaymentAuthorisationResponse(), statusCode, response));
    }

    private void updateAspspConsentData(PaymentWorkflow paymentWorkflow, HttpServletResponse httpResp) throws PaymentAuthorisationException {
        CmsAspspConsentDataBase64 consentData;
        try {
            consentData = new CmsAspspConsentDataBase64(paymentWorkflow.getPaymentId(), tokenStorageService.toBase64String(paymentWorkflow.getScaResponse()));
        } catch (IOException e) {
            throw new PaymentAuthorisationException(
                responseUtils.backToSender(new PaymentAuthorisationResponse(), paymentWorkflow.getPaymentResponse().getTppNokRedirectUri(),
                                           paymentWorkflow.getPaymentResponse().getTppOkRedirectUri(),
                                           httpResp, HttpStatus.INTERNAL_SERVER_ERROR, ValidationCode.CONSENT_DATA_UPDATE_FAILED));
        }
        ResponseEntity<?> updateAspspConsentData = aspspConsentDataClient.updateAspspConsentData(
            paymentWorkflow.getConsentReference().getEncryptedConsentId(), consentData);
        if (!HttpStatus.OK.equals(updateAspspConsentData.getStatusCode())) {
            throw new PaymentAuthorisationException(
                responseUtils.backToSender(new PaymentAuthorisationResponse(), paymentWorkflow.getPaymentResponse().getTppNokRedirectUri(),
                                           paymentWorkflow.getPaymentResponse().getTppOkRedirectUri(),
                                           httpResp, updateAspspConsentData.getStatusCode(), ValidationCode.CONSENT_DATA_UPDATE_FAILED));
        }
    }

    private void updatePaymentStatus(HttpServletResponse response, PaymentWorkflow paymentWorkflow)
        throws PaymentAuthorisationException {
        ResponseEntity<Void> updatePaymentStatus = cmsPsuPisClient.updatePaymentStatus(
            paymentWorkflow.getPaymentResponse().getPayment().getPaymentId(), paymentWorkflow.getPaymentStatus(), CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID);
        paymentWorkflow.getAuthResponse().updatePaymentStatus(TransactionStatusTO.valueOf(paymentWorkflow.getPaymentStatus()));
        if (!HttpStatus.OK.equals(updatePaymentStatus.getStatusCode())) {
            throw new PaymentAuthorisationException(responseUtils.couldNotProcessRequest(new PaymentAuthorisationResponse(), "Could not set payment status. See status code.", updatePaymentStatus.getStatusCode(), response));
        }
    }

}
