package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.psd2.sandbox.tpp.rest.server.config.IbanGenerationConfigProperties;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import lombok.RequiredArgsConstructor;
import org.iban4j.CountryCode;
import org.springframework.stereotype.Service;

import static de.adorsys.psd2.sandbox.tpp.rest.server.utils.IbanGenerator.*;

@Service
@RequiredArgsConstructor
public class IbanGenerationService {
    private final IbanGenerationConfigProperties properties;

    public String generateRandomIban() {
        return generateIban(CountryCode.valueOf(properties.getCountryCode()), properties.getBankCode().getRandom());
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
