package de.adorsys.ledgers.oba.rest.api.domain;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReference;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class PaymentWorkflow {
    private final CmsPaymentResponse paymentResponse;

    private String paymentStatus;
    private String authCodeMessage;
    private HttpStatus errorCode;
    private PaymentAuthorisationResponse authResponse;
    private final ConsentReference consentReference;
    private SCAResponseTO scaResponse;

    public PaymentWorkflow(@NotNull CmsPaymentResponse paymentResponse, ConsentReference consentReference) {
        if (paymentResponse == null || consentReference == null) {
            throw new IllegalStateException("Do not allow null input.");
        }
        this.paymentResponse = paymentResponse;
        this.consentReference = consentReference;
    }

    public BearerTokenTO getBearerToken() {
        return scaResponse == null
                   ? null
                   : scaResponse.getBearerToken();
    }

    public PaymentTypeTO getPaymentType() {
        return PaymentTypeTO.valueOf(paymentResponse.getPayment().getPaymentType().name());
    }

    public boolean isSingleScaMethod() {
        return scaResponse.getScaMethods() != null && scaResponse.getScaMethods().size() == 1;
    }

    public List<ScaUserDataTO> getScaMethods() {
        return scaResponse.getScaMethods();
    }

    public ScaStatusTO getScaStatus() {
        return scaResponse.getScaStatus();
    }

    public String getPaymentId() {
        return paymentResponse.getPayment().getPaymentId();
    }

    public String getAuthrisationId() {
        return paymentResponse.getAuthorisationId();
    }
}
