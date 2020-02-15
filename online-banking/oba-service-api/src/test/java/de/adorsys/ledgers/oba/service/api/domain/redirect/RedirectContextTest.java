package de.adorsys.ledgers.oba.service.api.domain.redirect;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaMethodTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.psd2.consent.api.ais.CmsAisAccountConsent;
import de.adorsys.psd2.consent.api.ais.CmsAisConsentResponse;
import de.adorsys.psd2.consent.api.pis.CmsCommonPayment;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import de.adorsys.psd2.xs2a.core.consent.AisConsentRequestType;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.EnumSet;

import static de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO.*;
import static de.adorsys.ledgers.oba.service.api.domain.redirect.RequestType.LOGIN;
import static de.adorsys.psd2.xs2a.core.pis.TransactionStatus.RCVD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RedirectContextTest {
    private static final String ENCRYPTED_ID = "ENCRYPTED";
    private static final String AUTH_ID = "AUTH";
    private static final String OK_URI = "OK";
    private static final String NOK_URI = "NOK";
    private static final String SEPA = "sepa-credit-transfers";
    private static final String REDIRECT_ID = "REDIRECT_ID";

    @Mock
    private CmsServiceStub cmsServiceStub;
    @Mock
    private MapperStub mapperStub;

    @Test
    public void fillCmsObject_Payment() {
        when(cmsServiceStub.getCmsResponse(any(), any())).thenReturn(getCmsPaymentResponse());
        when(mapperStub.getPisResponse(any(), any(), any())).thenReturn(new PaymentTO());

        RedirectContext context = new RedirectContext(LOGIN, ENCRYPTED_ID, AUTH_ID, PAYMENT, null, null, new BearerTokenTO());
        RedirectContext expected = getExpectedContextFillCmsObject(getPaymentTO(), PAYMENT);

        context.fillCmsObject(cmsServiceStub::getCmsResponse, mapperStub::getPisResponse);
        assertThat(context).isEqualTo(expected);
    }

    @Test
    public void fillCmsObject_Consent() {
        when(cmsServiceStub.getCmsResponse(any(), any())).thenReturn(getCmsAisResponse());

        RedirectContext context = new RedirectContext(LOGIN, ENCRYPTED_ID, AUTH_ID, CONSENT, null, null, new BearerTokenTO());
        RedirectContext expected = getExpectedContextFillCmsObject(getConsent(), CONSENT);

        context.fillCmsObject(cmsServiceStub::getCmsResponse, mapperStub::getPisResponse);
        assertThat(context).isEqualTo(expected);
    }


    @Test
    public void consentTypeRequiresAccounts_required_true() {
        RedirectContext context = new RedirectContext(LOGIN, ENCRYPTED_ID, AUTH_ID, CONSENT, null, null, new BearerTokenTO());
        context.setConsentRequestType(AisConsentRequestType.BANK_OFFERED);
        boolean result = context.consentTypeRequiresAccounts();
        assertThat(result).isTrue();
    }

    @Test
    public void consentTypeRequiresAccounts_required_false() {
        RedirectContext context = new RedirectContext(LOGIN, ENCRYPTED_ID, AUTH_ID, CONSENT, null, null, new BearerTokenTO());
        context.setConsentRequestType(AisConsentRequestType.DEDICATED_ACCOUNTS);
        boolean result = context.consentTypeRequiresAccounts();
        assertThat(result).isFalse();
    }

    @Test
    public void consentTypeRequiresAccounts_payment_request() {
        RedirectContext context = new RedirectContext(LOGIN, ENCRYPTED_ID, AUTH_ID, PAYMENT, null, null, new BearerTokenTO());
        boolean result = context.consentTypeRequiresAccounts();
        assertThat(result).isFalse();
    }

    @Test
    public void updateScaData() {
        RedirectContext context = getBasicContext(PAYMENT);
        context.updateScaData(getSCAResponseTO());

        assertThat(context).isEqualToComparingFieldByFieldRecursively(getExpectedContextUpdateScaData());
    }


    @Test
    public void failContext() {
        RedirectContext context = getBasicContext(PAYMENT);
        context.failContext();

        assertThat(context).isEqualToComparingFieldByFieldRecursively(getExpectedContextFailed());
    }

    private RedirectContext getExpectedContextFailed() {
        RedirectContext context = getBasicContext(PAYMENT);
        context.setBearer(null);
        context.setScaStatus(ScaStatusTO.FAILED);
        context.setTransactionStatus(TransactionStatusTO.CANC);
        return context;
    }

    private RedirectContext getExpectedContextUpdateScaData() {
        RedirectContext context = getBasicContext(PAYMENT);
        context.setScaDatas(Collections.singletonList(getScaUserData()));
        context.setBearer("");
        context.setScaStatus(ScaStatusTO.PSUIDENTIFIED);
        return context;
    }

    private SCAResponseTO getSCAResponseTO() {
        SCALoginResponseTO to = new SCALoginResponseTO();
        to.setBearerToken(new BearerTokenTO("", null, 1, null, null));
        to.setScaStatus(ScaStatusTO.PSUIDENTIFIED);
        to.setScaMethods(Collections.singletonList(getScaUserData()));
        return to;
    }

    private ScaUserDataTO getScaUserData() {
        return new ScaUserDataTO("", ScaMethodTypeTO.EMAIL, "", null, false, null, false, true);
    }

    private Object getCmsAisResponse() {
        return new CmsAisConsentResponse(getConsent(), AUTH_ID, OK_URI, NOK_URI);
    }

    private CmsAisAccountConsent getConsent() {
        CmsAisAccountConsent consent = new CmsAisAccountConsent();
        consent.setId(REDIRECT_ID);
        consent.setConsentStatus(ConsentStatus.RECEIVED);
        consent.setAisConsentRequestType(AisConsentRequestType.BANK_OFFERED);
        return consent;
    }

    private Object getPaymentTO() {
        PaymentTO payment = new PaymentTO();
        payment.setPaymentId(REDIRECT_ID);
        payment.setTransactionStatus(TransactionStatusTO.RCVD);
        return payment;
    }

    private Object getCmsPaymentResponse() {
        CmsCommonPayment payment = getCmsPayment();
        return new CmsPaymentResponse(payment, AUTH_ID, OK_URI, NOK_URI);
    }

    private CmsCommonPayment getCmsPayment() {
        CmsCommonPayment payment = new CmsCommonPayment(SEPA);
        payment.setPaymentType(PaymentType.SINGLE);
        payment.setPaymentId(REDIRECT_ID);
        payment.setTransactionStatus(RCVD);
        payment.setPaymentData(new byte[]{});
        return payment;
    }

    private RedirectContext getExpectedContextFillCmsObject(Object object, OpTypeTO opType) {
        RedirectContext context = getBasicContext(opType);

        context.setOkUri(OK_URI);
        context.setNokUri(NOK_URI);

        context.setRedirectId(REDIRECT_ID);

        if (EnumSet.of(PAYMENT, CANCEL_PAYMENT).contains(opType)) {
            context.setTransactionStatus(TransactionStatusTO.RCVD);
        }

        if (opType == CONSENT) {
            context.setScaStatus(ScaStatusTO.RECEIVED);
            context.setConsentRequestType(AisConsentRequestType.BANK_OFFERED);
        }

        context.setObject(object);
        return context;
    }

    private RedirectContext getBasicContext(OpTypeTO opType) {
        return new RedirectContext(LOGIN, ENCRYPTED_ID, AUTH_ID, opType, null, null, new BearerTokenTO());
    }

    private static class CmsServiceStub {
        public Object getCmsResponse(String one, String two) {
            return null;
        }
    }

    private static class MapperStub {
        public PaymentTO getPisResponse(String s, String s1, String s2) {
            return null;
        }

        public AisConsentTO getAisResponse(CmsAisAccountConsent s) {
            return null;
        }
    }

}
