package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.service.TokenStorageService;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentAuthorisationResponse;
import de.adorsys.ledgers.oba.rest.server.auth.MiddlewareAuthentication;
import de.adorsys.ledgers.oba.rest.server.service.CommonPisService;
import de.adorsys.ledgers.oba.rest.server.service.LoginService;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PisControllerTest {

    private static final String ENCRYPTED_PAYMENT_ID = "Encrypted payment id";
    private static final String AUTHORISATION_ID = "Payment id";
    private static final String LOGIN = "Login";
    private static final String PIN = "Pin";
    private static final String CONSENT_COOKIE_STRING = "Consent cookie string";

    @InjectMocks
    private PisController pisController;

    @Mock
    private PaymentRestClient paymentRestClient;
    @Mock
    private LoginService loginService;
    @Mock
    private CommonPisService commonPisService;
    @Mock
    protected AspspConsentDataClient aspspConsentDataClient;
    @Mock
    protected TokenStorageService tokenStorageService;
    @Mock
    protected AuthRequestInterceptor authInterceptor;
    @Mock
    protected HttpServletRequest request;
    @Mock
    protected HttpServletResponse response;
    @Mock
    protected MiddlewareAuthentication auth;

    @Before
    public void setUp() {

    }

    @Test
    public void preparePaymentForAuthorisation_Success() {
    }

    @Test
    public void login_Success() {
        //Given
        PaymentAuthorisationResponse expectedResult = buildPaymentAuthorisationResponse();
        HttpServletResponse expectedResponse = null;
        when(loginService.login(ENCRYPTED_PAYMENT_ID, AUTHORISATION_ID, LOGIN, PIN, CONSENT_COOKIE_STRING, expectedResponse))
            .thenReturn(ResponseEntity.ok(expectedResult));

        //When
        ResponseEntity<PaymentAuthorisationResponse> actualResult = pisController.login(ENCRYPTED_PAYMENT_ID, AUTHORISATION_ID, LOGIN, PIN, CONSENT_COOKIE_STRING);

        //Then
        assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actualResult.getBody()).isEqualTo(expectedResult);
    }

    @Test
    public void initiatePayment_Success() {
    }

    @Test
    public void selectScaMethod_Success() {
    }

    @Test
    public void authorisePayment_Success() {
    }

    private PaymentAuthorisationResponse buildPaymentAuthorisationResponse() {
        PaymentAuthorisationResponse paymentAuthorisationResponse = new PaymentAuthorisationResponse();
        return paymentAuthorisationResponse;
    }
}
