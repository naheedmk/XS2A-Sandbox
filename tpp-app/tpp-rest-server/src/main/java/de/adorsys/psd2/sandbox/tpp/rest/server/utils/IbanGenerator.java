package de.adorsys.psd2.sandbox.tpp.rest.server.utils;

import org.iban4j.CountryCode;
import org.iban4j.Iban;

public class IbanGenerator {

    public static String generateIban(CountryCode countryCode, String bankCode) {
        return new Iban.Builder()
                   .countryCode(countryCode)
                   .bankCode(bankCode)
                   .buildRandom()
                   .toString();
    }

    public static String generateIbanWithCountryCode(CountryCode countryCode) {
        return new Iban.Builder()
                   .countryCode(countryCode)
                   .buildRandom()
                   .toString();
    }
}
