package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.client.rest.AccountRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.config.IbanGenerationConfigProperties;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.CountryCode;
import org.springframework.stereotype.Service;

import static de.adorsys.psd2.sandbox.tpp.rest.server.utils.IbanGenerator.generateIban;
import static de.adorsys.psd2.sandbox.tpp.rest.server.utils.IbanGenerator.generateIbanWithCountryCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class IbanGenerationService {
    private final IbanGenerationConfigProperties properties;
    private final AccountRestClient accountRestClient;

    public String generateRandomIban() {
        String iban = null;
        try {
            while (true) {
                iban = generateIban(CountryCode.valueOf(properties.getCountryCode()), properties.getBankCode().getRandom());
                accountRestClient.getAccountDetailsByIban(iban);
            }
        } catch (FeignException e) {
            log.info("Generated IBAN: " + iban);
        }
        return iban;
    }

    public String generateRandomIbanWithCountryCode(CountryCode countryCode) {
        return generateIbanWithCountryCode(countryCode);
    }

    public String generateIbanForNisp(DataPayload payload, String iban) {
        if (payload.getGeneratedIbans().containsKey(iban)) {
            return payload.getGeneratedIbans().get(iban);
        }
        String generatedIban = generateIban(CountryCode.valueOf(properties.getCountryCode()), properties.getBankCode().getNisp());
        payload.getGeneratedIbans().put(iban, generatedIban);
        return generatedIban;
    }
}
