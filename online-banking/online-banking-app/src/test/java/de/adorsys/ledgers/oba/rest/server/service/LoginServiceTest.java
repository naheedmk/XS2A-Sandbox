package de.adorsys.ledgers.oba.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReference;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentType;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentAuthorisationResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.rest.api.exception.PaymentAuthorisationException;
import de.adorsys.ledgers.oba.rest.server.resource.utils.ResponseUtils;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginServiceTest {

    private static final String ENCRYPTED_PAYMENT_ID = "Encrypted payment id";
    private static final String AUTHORISATION_ID = "Payment id";
    private static final String LOGIN = "Login";
    private static final String PIN = "Pin";
    private static final String CONSENT_COOKIE_STRING = "Consent cookie string";
    private static final String PAYMENT_PRODUCT = "Payment product";

    @InjectMocks
    LoginService loginService;

    @Mock
    private UserMgmtRestClient userManagementRestClient;
    @Mock
    private ResponseUtils responseUtils;
    @Mock
    private CommonPisService commonPisService;

    @Before
    public void setUp() throws PaymentAuthorisationException {
        doNothing().when(commonPisService).processPaymentResponse(any(PaymentWorkflow.class), any(SCAPaymentResponseTO.class));
        doNothing().when(commonPisService).scaStatus(any(PaymentWorkflow.class), any(String.class), any(HttpServletResponse.class));
        doNothing().when(commonPisService).initiatePayment(any(PaymentWorkflow.class), any(HttpServletResponse.class));
        doNothing().when(commonPisService).selectMethod(any(String.class), any(PaymentWorkflow.class));
        doNothing().when(commonPisService).updateScaStatusPaymentStatusConsentData(any(String.class), any(PaymentWorkflow.class), any(HttpServletResponse.class));
    }

    @Test
    public void login_Success() throws PaymentAuthorisationException {
        //Given
        HttpServletResponse response = new MockHttpServletResponse();
        PaymentAuthorisationResponse expectedResult = buildPaymentAuthorisationResponse();
        PaymentWorkflow expectedPaymentWorkflow = buildPaymentWorkflow();
        ResponseEntity<SCALoginResponseTO> expectedScaLoginResponseEntity = buildScaLoginResponseEntity();
        when(commonPisService.identifyPayment(ENCRYPTED_PAYMENT_ID, AUTHORISATION_ID, false, CONSENT_COOKIE_STRING, LOGIN, response, null))
            .thenReturn(expectedPaymentWorkflow);
        when(userManagementRestClient.authoriseForConsent(LOGIN, PIN, ENCRYPTED_PAYMENT_ID, AUTHORISATION_ID, OpTypeTO.PAYMENT))
            .thenReturn(expectedScaLoginResponseEntity);

        //When
        ResponseEntity<PaymentAuthorisationResponse> actualResult = loginService.login(ENCRYPTED_PAYMENT_ID, AUTHORISATION_ID, LOGIN, PIN, CONSENT_COOKIE_STRING, response);

        //Then
        assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actualResult.getBody()).isEqualTo(expectedResult);
    }

    @Test
    public void login_Fail() {

    }

    private PaymentAuthorisationResponse buildPaymentAuthorisationResponse() {
        PaymentAuthorisationResponse response = new PaymentAuthorisationResponse();
        return response;
    }

    private PaymentWorkflow buildPaymentWorkflow() {
        PaymentWorkflow paymentWorkflow = new PaymentWorkflow(buildCmsPaymentResponse(), buildConsentReference());
        return paymentWorkflow;
    }

    private ResponseEntity<SCALoginResponseTO> buildScaLoginResponseEntity() {
        SCALoginResponseTO scaLoginResponseTO = new SCALoginResponseTO();
        scaLoginResponseTO.setAuthorisationId(AUTHORISATION_ID);
        return ResponseEntity.ok(scaLoginResponseTO);
    }

    private CmsPaymentResponse buildCmsPaymentResponse() {
        CmsPaymentResponse cmsPaymentResponse = new CmsPaymentResponse();
        cmsPaymentResponse.setAuthorisationId(AUTHORISATION_ID);

        CmsSinglePayment cmsPayment = new CmsSinglePayment(PAYMENT_PRODUCT);
        cmsPayment.setPaymentId(ENCRYPTED_PAYMENT_ID);

        cmsPaymentResponse.setPayment(cmsPayment);

        return cmsPaymentResponse;
    }

    private ConsentReference buildConsentReference() {
        ConsentReference consentReference = new ConsentReference();
        consentReference.setAuthorisationId(AUTHORISATION_ID);
        consentReference.setConsentType(ConsentType.PIS);
        consentReference.setCookieString(CONSENT_COOKIE_STRING);
        consentReference.setEncryptedConsentId(ENCRYPTED_PAYMENT_ID);

        return consentReference;
    }
}
