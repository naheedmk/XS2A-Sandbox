package de.adorsys.ledgers.oba.service.impl.service.redirect;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.AccountStatusTO;
import de.adorsys.ledgers.middleware.api.domain.account.AccountTypeTO;
import de.adorsys.ledgers.middleware.api.domain.account.UsageTypeTO;
import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaMethodTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.oba.service.api.domain.redirect.RedirectContext;
import de.adorsys.ledgers.oba.service.api.domain.redirect.RequestType;
import de.adorsys.ledgers.oba.service.api.service.redirect.CmsRedirectService;
import de.adorsys.ledgers.oba.service.api.service.redirect.LedgersAccountInformationService;
import de.adorsys.ledgers.oba.service.api.service.redirect.LedgersAuthorizationService;
import de.adorsys.psd2.xs2a.core.consent.AisConsentRequestType;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import de.adorsys.psd2.xs2a.core.profile.AccountReferenceType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Currency;
import java.util.EnumSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RedirectCommonServiceImplTest {
    private static final String ENCRYPTED_ID = "ENCRYPTED";
    private static final String AUTH_ID = "AUTH";
    private static final String OK_URI = "OK";
    private static final String NOK_URI = "NOK";
    private static final String SEPA = "sepa-credit-transfers";
    private static final String REDIRECT_ID = "REDIRECT_ID";
    private static final Currency EUR = Currency.getInstance("EUR");

    @InjectMocks
    private RedirectCommonServiceImpl service;

    @Mock
    private CmsRedirectService cmsRedirectService;
    @Mock
    private LedgersAuthorizationService ledgersAuthorizationService;
    @Mock
    private LedgersAccountInformationService ledgersAccountInformationService;

    @Test
    public void loginForOperation_consent() {
        when(ledgersAuthorizationService.loginForConsent(anyString(), anyString(), any())).thenReturn(getLoginContext(RequestType.LOGIN, OpTypeTO.CONSENT));
        when(ledgersAccountInformationService.getAccounts(any())).thenReturn(getDetails());

        RedirectContext result = service.loginForOperation(getStartContext(RequestType.LOGIN, OpTypeTO.CONSENT, null), "login", "pin");

        assertThat(result).isEqualToComparingFieldByFieldRecursively(getLoginExpectedContext(OpTypeTO.CONSENT, AisConsentRequestType.BANK_OFFERED));
    }

    @Test
    public void loginForOperation_payment() {
        when(ledgersAuthorizationService.loginForConsent(anyString(), anyString(), any())).thenReturn(getLoginContext(RequestType.LOGIN, OpTypeTO.PAYMENT));

        RedirectContext result = service.loginForOperation(getStartContext(RequestType.LOGIN, OpTypeTO.PAYMENT, null), "login", "pin");

        assertThat(result).isEqualToComparingFieldByFieldRecursively(getLoginExpectedContext(OpTypeTO.PAYMENT, null));
    }

    @Test
    public void initOperation() {
    }

    @Test
    public void selectMethod() {
    }

    @Test
    public void authorizeOperation() {
    }

    @Test
    public void failOperation() {
    }

    private RedirectContext getLoginContext(RequestType requestType, OpTypeTO opType) {
        RedirectContext context = getStartContext(requestType, opType, null);
        context.setRedirectId(REDIRECT_ID);
        if (opType == OpTypeTO.CONSENT) {
            context.setConsentRequestType(AisConsentRequestType.BANK_OFFERED);
        }
        if (opType == OpTypeTO.PAYMENT) {
            context.setTransactionStatus(TransactionStatusTO.RCVD);
        }
        context.setObject(new Object());
        context.setBearer("");
        context.setScaDatas(getScaDatas());
        context.setScaStatus(ScaStatusTO.PSUIDENTIFIED);
        context.setOkUri(OK_URI);
        context.setNokUri(NOK_URI);
        return context;
    }

    private RedirectContext getLoginExpectedContext(OpTypeTO opType, AisConsentRequestType consentRequestType) {
        RedirectContext context = getStartContext(RequestType.LOGIN, opType, new Object());
        context.setScaStatus(ScaStatusTO.PSUIDENTIFIED);
        context.setRedirectId(REDIRECT_ID);
        context.setBearer("");
        context.setObject(new Object());
        context.setScaDatas(getScaDatas());
        context.setOkUri(OK_URI);
        context.setNokUri(NOK_URI);
        context.setConsentRequestType(consentRequestType);
        if (opType == OpTypeTO.CONSENT && context.getConsentRequestType() != AisConsentRequestType.DEDICATED_ACCOUNTS) {
            context.setAccountDetails(getReferences());
        }
        if (EnumSet.of(OpTypeTO.PAYMENT, OpTypeTO.CANCEL_PAYMENT).contains(opType)) {
            context.setTransactionStatus(TransactionStatusTO.RCVD);
        }
        return context;
    }

    private List<AccountReference> getReferences() {
        return Collections.singletonList(new AccountReference(AccountReferenceType.IBAN, "iban", EUR));
    }

    private List<AccountDetailsTO> getDetails() {
        return Collections.singletonList(new AccountDetailsTO("id", "iban", null, null, null, null, EUR, "name", null, AccountTypeTO.CASH, AccountStatusTO.ENABLED, null, null, UsageTypeTO.PRIV, null, Collections.emptyList()));
    }

    private List<ScaUserDataTO> getScaDatas() {
        return Collections.singletonList(new ScaUserDataTO("id", ScaMethodTypeTO.EMAIL, "val@val.de", null, false, null, false, true));
    }

    private RedirectContext getStartContext(RequestType requestType, OpTypeTO opType, Object obj) {
        return new RedirectContext(requestType, ENCRYPTED_ID, AUTH_ID, opType, obj, null, new BearerTokenTO());
    }
}
