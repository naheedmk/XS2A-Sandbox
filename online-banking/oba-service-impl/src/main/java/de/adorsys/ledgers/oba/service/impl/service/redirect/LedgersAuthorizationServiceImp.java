package de.adorsys.ledgers.oba.service.impl.service.redirect;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import de.adorsys.ledgers.middleware.api.domain.sca.*;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.ledgers.middleware.client.rest.ConsentRestClient;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.service.api.domain.redirect.RedirectContext;
import de.adorsys.ledgers.oba.service.api.domain.redirect.TriFunction;
import de.adorsys.ledgers.oba.service.api.service.redirect.LedgersAuthorizationService;
import de.adorsys.ledgers.oba.service.impl.mapper.ObaAisConsentMapper;
import de.adorsys.psd2.consent.api.ais.CmsAisAccountConsent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LedgersAuthorizationServiceImp implements LedgersAuthorizationService {
    private final UserMgmtRestClient userMgmtRestClient;
    private final PaymentRestClient paymentRestClient;
    private final ConsentRestClient consentRestClient;
    private final ObaAisConsentMapper consentMapper;

    @Override
    public RedirectContext loginForConsent(String login, String pin, RedirectContext context) {
        SCALoginResponseTO response = userMgmtRestClient.authoriseForConsent(login, pin, context.getRedirectId(), context.getRedirectId(), context.getOpType()).getBody();
        return context.updateScaData(response);
    }

    @Override
    public RedirectContext initOperation(RedirectContext context) {
        SCAResponseTO response;
        if (context.getOpType() == OpTypeTO.CONSENT) {
            AisConsentTO consent = consentMapper.toTo((CmsAisAccountConsent) context.getObject());
            response = consentRestClient.startSCA(context.getRedirectId(), consent).getBody();
        } else {
            response = context.getOpType() == OpTypeTO.CANCEL_PAYMENT
                           ? paymentRestClient.initiatePmtCancellation(context.getRedirectId()).getBody()
                           : initPayment(context);
        }

        return context.updateScaData(response);
    }

    @Override
    public RedirectContext selectMethod(RedirectContext context) {
        return authFunction(consentRestClient::selectMethod, paymentRestClient::selecCancelPaymentSCAtMethod, paymentRestClient::selectMethod, context);
    }

    @Override
    public RedirectContext validateTan(RedirectContext context) {
        return authFunction(consentRestClient::authorizeConsent, paymentRestClient::authorizeCancelPayment, paymentRestClient::authorizePayment, context);
    }

    private RedirectContext authFunction(TriFunction<String, String, String, ResponseEntity<SCAConsentResponseTO>> consentFunction,
                                         TriFunction<String, String, String, ResponseEntity<SCAPaymentResponseTO>> cancellationFunction,
                                         TriFunction<String, String, String, ResponseEntity<SCAPaymentResponseTO>> paymentFunction,
                                         RedirectContext context) {
        SCAResponseTO response;
        if (context.getOpType() == OpTypeTO.CONSENT) {
            response = consentFunction.apply(context.getRedirectId(), context.getRedirectId(), context.getRequestType().resolveVariableByRequestType(context)).getBody();
        } else {
            response = context.getOpType() == OpTypeTO.CANCEL_PAYMENT
                           ? cancellationFunction.apply(context.getRedirectId(), context.getRedirectId(), context.getRequestType().resolveVariableByRequestType(context)).getBody()
                           : paymentFunction.apply(context.getRedirectId(), context.getRedirectId(), context.getRequestType().resolveVariableByRequestType(context)).getBody();
        }
        return context.updateScaData(response);
    }

    private SCAPaymentResponseTO initPayment(RedirectContext context) {
        PaymentTO payment = (PaymentTO) context.getObject();
        return paymentRestClient.initiatePayment(payment.getPaymentType(), payment).getBody();
    }
}
