package de.adorsys.ledgers.oba.service.impl.service.redirect;

import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.client.mappers.PaymentMapperTO;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.domain.redirect.RedirectContext;
import de.adorsys.ledgers.oba.service.api.service.redirect.CmsRedirectService;
import de.adorsys.psd2.consent.api.AspspDataService;
import de.adorsys.psd2.consent.api.WrongChecksumException;
import de.adorsys.psd2.consent.api.ais.CmsAisAccountConsent;
import de.adorsys.psd2.consent.api.ais.CmsAisConsentResponse;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import de.adorsys.psd2.consent.psu.api.CmsPsuAisService;
import de.adorsys.psd2.consent.psu.api.CmsPsuPisService;
import de.adorsys.psd2.consent.psu.api.ais.CmsAisConsentAccessRequest;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.core.exception.AuthorisationIsExpiredException;
import de.adorsys.psd2.xs2a.core.exception.RedirectUrlIsExpiredException;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.sca.AuthenticationDataHolder;
import de.adorsys.psd2.xs2a.core.sca.ScaStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO.CONSENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class CmsRedirectServiceImpl implements CmsRedirectService {
    private static final String INSTANCE_ID = "UNDEFINED";
    private final CmsPsuPisService cmsPsuPisService;
    private final CmsPsuAisService cmsPsuAisService;
    private final AspspDataService aspspDataService;
    private final PaymentMapperTO paymentMapper;

    @Override
    public void getCmsObject(RedirectContext context) {
        context.fillCmsObject(context.getOpType() == CONSENT
                                  ? this::getConsentResponse
                                  : this::getPaymentResponse,
            paymentMapper::toAbstractPayment);
    }

    @Override
    public boolean updateCmsDataWithAccess(RedirectContext context) {
        try {
            if (context.getOpType() != CONSENT) {
                return updateCmsForPaymentOp(context);
            } else {
                return updateCmsForConsent(context) && cmsPsuAisService.updateAccountAccessInConsent(context.getRedirectId(), mapToCmsAisAccessRequest(context), INSTANCE_ID);
            }
        } catch (AuthorisationIsExpiredException e) {
            getAuthExpiredError("consent", context.getEncryptedId(), context.getRedirectId());
        }
        return false;
    }

    @Override
    public boolean cmsStatusesUpdate(RedirectContext context) {
        try {
            if (context.getOpType() != CONSENT) {
                return updateCmsForPaymentOp(context);
            } else {
                return updateCmsForConsent(context) && updateConsentStatus(context);
            }
        } catch (AuthorisationIsExpiredException e) {
            getAuthExpiredError("consent", context.getEncryptedId(), context.getRedirectId());
        }
        return false;
    }

    private boolean updateConsentStatus(RedirectContext context) {
        try {
            if (context.getScaStatus() == ScaStatusTO.FINALISED) {
                return cmsPsuAisService.confirmConsent(context.getRedirectId(), INSTANCE_ID);
            }
            if (context.getScaStatus() == ScaStatusTO.FAILED) {
                return cmsPsuAisService.rejectConsent(context.getRedirectId(), INSTANCE_ID);
            }
        } catch (WrongChecksumException e) {
            throw new ObaException("Invalid checksum in CMS!", ObaErrorCode.CMS_UPDATE_ERROR);
        }
        return true;
    }

    private boolean updateCmsForConsent(RedirectContext context) throws AuthorisationIsExpiredException {
        boolean updateAspspConsentData = aspspDataService.updateAspspConsentData(new AspspConsentData(context.getBearer().getBytes(), context.getRedirectId()));
        PsuIdData idData = new PsuIdData(context.getPsuId(), null, null, null, null);
        boolean updateAuthorisationStatus = cmsPsuAisService.updateAuthorisationStatus(idData, context.getRedirectId(), context.getAuthorizationId(), ScaStatus.valueOf(context.getScaStatus().name()), INSTANCE_ID, new AuthenticationDataHolder(null, null));
        return updateAuthorisationStatus && updateAspspConsentData;
    }

    private boolean updateCmsForPaymentOp(RedirectContext context) throws AuthorisationIsExpiredException {
        boolean updatePaymentStatus = cmsPsuPisService.updatePaymentStatus(context.getRedirectId(), TransactionStatus.valueOf(context.getTransactionStatus().name()), INSTANCE_ID);
        PsuIdData idData = new PsuIdData(context.getPsuId(), null, null, null, null);
        boolean updateAuthorisationStatus = cmsPsuPisService.updateAuthorisationStatus(idData, context.getRedirectId(), context.getAuthorizationId(), ScaStatus.valueOf(context.getScaStatus().name()), INSTANCE_ID, new AuthenticationDataHolder(null, null));
        return updatePaymentStatus && updateAuthorisationStatus;
    }

    private CmsAisConsentAccessRequest mapToCmsAisAccessRequest(RedirectContext context) {
        CmsAisAccountConsent consent = (CmsAisAccountConsent) context.getObject();
        return new CmsAisConsentAccessRequest(consent.getAccess(), consent.getValidUntil(), consent.getFrequencyPerDay(), false, false);
    }

    private CmsAisConsentResponse getConsentResponse(String encryptedId, String authId) {
        try {
            return cmsPsuAisService.checkRedirectAndGetConsent(authId, INSTANCE_ID)
                       .orElseThrow(() -> new ObaException(String.format("Consent %s not found!", encryptedId), ObaErrorCode.NOT_FOUND));
        } catch (RedirectUrlIsExpiredException e) {
            getAuthExpiredError("consent", encryptedId, authId);
        }
        return null;
    }

    private CmsPaymentResponse getPaymentResponse(String encryptedId, String authId) {
        try {
            return cmsPsuPisService.checkRedirectAndGetPayment(authId, INSTANCE_ID)
                       .orElseThrow(() -> new ObaException(String.format("Payment %s not found!", encryptedId), ObaErrorCode.NOT_FOUND));
        } catch (RedirectUrlIsExpiredException e) {
            getAuthExpiredError("payment", encryptedId, authId);
        }
        return null;
    }

    private void getAuthExpiredError(String errorObject, String encryptedId, String authId) {
        log.error("Could not Extract {} from CMS, id: {}, reason: authorization expired!", errorObject, encryptedId);
        throw new ObaException(String.format("Authorization %s is expired!", authId), ObaErrorCode.NOT_FOUND);
    }
}
