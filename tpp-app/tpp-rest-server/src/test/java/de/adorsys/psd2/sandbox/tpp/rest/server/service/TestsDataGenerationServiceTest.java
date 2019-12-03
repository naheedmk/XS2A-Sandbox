package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.SinglePaymentTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.AccountRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.config.IbanGenerationConfigProperties;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.AccountBalance;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.apache.commons.lang3.StringUtils;
import org.iban4j.CountryCode;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestsDataGenerationServiceTest {
    private static final String TPP_ID = "11111111";
    private static final String COUNTRY_CODE = "DE";
    private static final String INVALID_COUNTRY_CODE = "EEE";
    private static final String BANK_CODE = "76050101";

    @InjectMocks
    private IbanGenerationService generationService;
    @Mock
    private AccountRestClient accountRestClient;
    @Mock
    private IbanGenerationConfigProperties ibanGenerationConfigProperties;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        ibanGenerationConfigProperties = new IbanGenerationConfigProperties();
        ibanGenerationConfigProperties.getBankCode().setNisp(BANK_CODE);
        ibanGenerationConfigProperties.getBankCode().setRandom(BANK_CODE);
        ibanGenerationConfigProperties.setCountryCode(COUNTRY_CODE);
        generationService = new IbanGenerationService(ibanGenerationConfigProperties, accountRestClient);
    }

    @Test
    public void generateRandomIban() {
        when(accountRestClient.getAccountDetailsByIban(anyString())).thenThrow(getFeignException());
        String iban = generationService.generateRandomIban();
        assertTrue(StringUtils.isNotEmpty(iban));
    }

    @Test
    public void generateRandomIbanWithCountryCode() {
        String iban = generationService.generateRandomIbanWithCountryCode(CountryCode.valueOf(COUNTRY_CODE));
        assertTrue(StringUtils.isNoneEmpty(iban));
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateRandomIbanWithCountryCode_wrongCountryCode() {
        when(generationService.generateRandomIbanWithCountryCode(CountryCode.valueOf(INVALID_COUNTRY_CODE))).thenThrow(IllegalArgumentException.class);
        generationService.generateRandomIbanWithCountryCode(CountryCode.valueOf(anyString()));
    }

    @Test
    public void generateNispIban() {
        String s = generationService.generateIbanForNisp(getPayload(), "00");
        assertTrue(StringUtils.isNotBlank(s));
    }

    private DataPayload getPayload() {
        List<UserTO> users = Collections.singletonList(new UserTO("login", "email", "pin"));
        List<AccountDetailsTO> accounts = Collections.singletonList(new AccountDetailsTO());
        List<AccountBalance> balances = Collections.singletonList(new AccountBalance());
        List<SinglePaymentTO> payments = Collections.singletonList(new SinglePaymentTO());
        return new DataPayload(users, accounts, balances, payments, false, TPP_ID, new HashMap<>());
    }

    @NotNull
    private FeignException getFeignException() {
        Request rec = Request.create(Request.HttpMethod.GET, " ", new HashMap<>(), Request.Body.empty());
        Response resp = Response.builder().status(404).request(rec).headers(new HashMap<>()).build();
        return FeignException.errorStatus("GET", resp);
    }
}
