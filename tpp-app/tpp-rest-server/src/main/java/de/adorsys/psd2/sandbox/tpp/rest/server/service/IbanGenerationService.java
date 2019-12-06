package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.TppData;
import lombok.RequiredArgsConstructor;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.iban4j.bban.BbanStructure;
import org.iban4j.bban.BbanStructureEntry;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.iban4j.bban.BbanEntryType.account_number;
import static org.iban4j.bban.BbanEntryType.bank_code;

@Service
@RequiredArgsConstructor
public class IbanGenerationService {

    private final UserMgmtRestClient userMgmtRestClient;

    public String generateNextIban() {
        TppData data = getTppData();
        return generateIban(data.getCountryCode(), data.getBranchId(), data.getNextAccountNumber());
    }

    public String generateIbanForNisp(DataPayload payload, String iban) {
        if (payload.getGeneratedIbans().containsKey(iban)) {
            return payload.getGeneratedIbans().get(iban);
        }
        TppData data = getTppData();
        String generatedIban = generateIban(data.getCountryCode(), data.getBranchId(), Long.parseLong(iban));
        payload.getGeneratedIbans().put(iban, generatedIban);
        return generatedIban;
    }

    public List<CountryCode> getSupportedCountryCodes() {
        return BbanStructure.supportedCountries();
    }

    public Integer getBankCodeLength(CountryCode countryCode) {
        return BbanStructure.forCountry(countryCode).getEntries().get(0).getLength();
    }

    public String getBankCodeCharacterType(CountryCode countryCode) {
        return BbanStructure.forCountry(countryCode).getEntries().stream()
                   .filter(e -> e.getEntryType().equals(bank_code)).findFirst().map(BbanStructureEntry::getCharacterType).get().toString();
    }

    private TppData getTppData() {
        return new TppData(userMgmtRestClient.getUser().getBody());
    }

    private String generateIban(CountryCode countryCode, String bankCode, long accountNr) {
        int accountNumberLength = BbanStructure.forCountry(countryCode).getEntries().stream()
                                      .filter(e -> e.getEntryType().equals(account_number))
                                      .findFirst().get().getLength();
        String formatParam = "%0" + accountNumberLength + "d";
        String accountNumber = String.format(formatParam, accountNr);
        return new Iban.Builder()
                   .countryCode(countryCode)
                   .bankCode(bankCode)
                   .accountNumber(accountNumber)
                   .buildRandom()
                   .toString();
    }
}
