package de.adorsys.ledgers.oba.service.api.domain.redirect;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.psd2.consent.api.ais.AisAccountAccess;
import de.adorsys.psd2.consent.api.ais.CmsAisAccountConsent;
import de.adorsys.psd2.consent.api.ais.CmsAisConsentResponse;
import de.adorsys.psd2.consent.api.pis.CmsCommonPayment;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import de.adorsys.psd2.xs2a.core.consent.AisConsentRequestType;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import de.adorsys.psd2.xs2a.core.profile.AccountReferenceType;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO.*;
import static de.adorsys.psd2.xs2a.core.consent.AisConsentRequestType.ALL_AVAILABLE_ACCOUNTS;
import static de.adorsys.psd2.xs2a.core.consent.AisConsentRequestType.GLOBAL;

@Data
public class RedirectContext {
    private String psuId;
    private RequestType requestType;
    private String encryptedId;
    private String authorizationId;
    private String redirectId;
    private String selectedScaMethodId;
    private String tan;
    private OpTypeTO opType;
    private ScaStatusTO scaStatus;
    private TransactionStatusTO transactionStatus;
    private List<ScaUserDataTO> scaDatas;
    private List<AccountReference> accountDetails;
    private Object object;
    private String bearer;
    private AisConsentRequestType consentRequestType;
    private String okUri;
    private String nokUri;

    public RedirectContext(RequestType requestType, String encryptedId, String authorizationId, OpTypeTO opType, Object object, String psuId, BearerTokenTO bearerToken) {
        this.requestType = requestType;
        this.encryptedId = encryptedId;
        this.authorizationId = authorizationId;
        this.opType = opType;
        requestType.setContextFieldByRequestType(this, object);
        this.psuId = psuId;
        this.bearer = Optional.ofNullable(bearerToken)
                          .map(BearerTokenTO::getAccess_token)
                          .orElse(null);
    }

    @JsonIgnore
    public void fillCmsObject(BiFunction<String, String, Object> supplyResponseFunction, TriFunction<String, String, String, PaymentTO> paymentMapper) {
        if (this.opType == CONSENT) {
            CmsAisConsentResponse response = (CmsAisConsentResponse) supplyResponseFunction.apply(encryptedId, authorizationId);
            mapOkNokUris(response::getTppOkRedirectUri, response::getTppNokRedirectUri);

            CmsAisAccountConsent consent = response.getAccountConsent();
            this.redirectId = consent.getId();
            Optional.ofNullable(consent.getConsentStatus())
                .map(Enum::name)
                .map(ScaStatusTO::valueOf)
                .ifPresent(this::setScaStatus);
            this.consentRequestType = consent.getAisConsentRequestType();
            this.object = Optional.ofNullable(this.object)
                              .orElse(consent);
        } else {
            CmsPaymentResponse response = (CmsPaymentResponse) supplyResponseFunction.apply(encryptedId, authorizationId);
            mapOkNokUris(response::getTppOkRedirectUri, response::getTppNokRedirectUri);

            CmsCommonPayment payment = (CmsCommonPayment) response.getPayment();
            this.redirectId = payment.getPaymentId();
            this.transactionStatus = TransactionStatusTO.valueOf(payment.getTransactionStatus().name());
            PaymentTO paymentTO = paymentMapper.apply(new String(payment.getPaymentData(), StandardCharsets.UTF_8), payment.getPaymentType().name(), payment.getPaymentProduct());
            paymentTO.setPaymentId(payment.getPaymentId());
            paymentTO.setTransactionStatus(TransactionStatusTO.valueOf(payment.getTransactionStatus().name()));
            this.object = paymentTO;
        }
    }

    private void mapOkNokUris(Supplier<String> okUriSupplier, Supplier<String> nokUriSupplier) {
        this.okUri = okUriSupplier.get();
        this.nokUri = nokUriSupplier.get();
    }

    @JsonIgnore
    public boolean consentTypeRequiresAccounts() {
        return this.opType == CONSENT
                   && this.consentRequestType != AisConsentRequestType.DEDICATED_ACCOUNTS;
    }

    @JsonIgnore
    public RedirectContext updateScaData(SCAResponseTO response) {
        this.bearer = Optional.ofNullable(response.getBearerToken())
                          .map(BearerTokenTO::getAccess_token)
                          .orElse(this.bearer);
        this.scaStatus = response.getScaStatus();
        this.scaDatas = response.getScaMethods();

        if (EnumSet.of(PAYMENT, CANCEL_PAYMENT).contains(this.opType) && this.requestType != RequestType.LOGIN) {
            SCAPaymentResponseTO paymentResponse = (SCAPaymentResponseTO) response;
            this.transactionStatus = paymentResponse.getTransactionStatus();
        }
        return this;
    }

    @JsonIgnore
    public void failContext() {
        this.scaStatus = ScaStatusTO.FAILED;
        this.transactionStatus = TransactionStatusTO.CANC;
        this.bearer = null;
    }

    public void updateAccountsAndConsent(Function<RedirectContext, List<AccountDetailsTO>> supplyAccounts) {
        if (consentTypeRequiresAccounts()) {
            this.accountDetails = toCmsReferences(supplyAccounts.apply(this));
            if (EnumSet.of(ALL_AVAILABLE_ACCOUNTS, GLOBAL).contains(this.consentRequestType)) {
                updateAccess();
            }
        }
    }

    private List<AccountReference> toCmsReferences(List<AccountDetailsTO> details) {
        return details.stream()
                   .map(d -> new AccountReference(AccountReferenceType.IBAN, d.getIban(), d.getCurrency()))
                   .collect(Collectors.toList());
    }

    private void updateAccess() {
        CmsAisAccountConsent consent = (CmsAisAccountConsent) this.object;
        AisAccountAccess cmsAccess = consent.getAccess();
        AisAccountAccess access;
        if (this.consentRequestType == ALL_AVAILABLE_ACCOUNTS) {
            access = new AisAccountAccess(this.accountDetails, null, null, cmsAccess.getAvailableAccounts(), cmsAccess.getAllPsd2(), cmsAccess.getAvailableAccountsWithBalance(), cmsAccess.getAccountAdditionalInformationAccess());
        } else {
            access = new AisAccountAccess(this.accountDetails, this.accountDetails, this.accountDetails, cmsAccess.getAvailableAccounts(), cmsAccess.getAllPsd2(), cmsAccess.getAvailableAccountsWithBalance(), cmsAccess.getAccountAdditionalInformationAccess());
        }
        consent.setAccess(access);
        this.object = consent;
    }
}
