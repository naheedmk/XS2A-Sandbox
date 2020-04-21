package de.adorsys.ledgers.oba.service.impl.mapper;

import de.adorsys.ledgers.middleware.api.domain.account.AccountReferenceTO;
import de.adorsys.ledgers.middleware.api.domain.general.AddressTO;
import de.adorsys.ledgers.middleware.api.domain.payment.AmountTO;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentProductTO;
import de.adorsys.ledgers.middleware.api.domain.payment.SinglePaymentTO;
import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.psd2.consent.api.CmsAddress;
import de.adorsys.psd2.consent.api.pis.CmsAmount;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import de.adorsys.psd2.xs2a.core.pis.PisDayOfExecution;
import de.adorsys.psd2.xs2a.core.pis.PisExecutionRule;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SinglePaymentMapperTest {
    private static final String PAYMENT_ID = "QWERTY";
    private static final String END_TO_EN_ID = "YTREWQ";
    private static final String CREDITOR_AGENT = "Agent";
    private static final String CREDITOR_NAME = "Steve Jobs";
    private static final String REMITTANCE = "Here we have some priceless information on the payment";
    private static final LocalDate REQUESTED_DATE = LocalDate.of(2019, 7, 3);
    private static final LocalTime REQUESTED_TIME = LocalTime.of(21, 20);
    private static final String IBAN = "DE12345";
    private static final String BBAN = "33654";
    private static final String PAN = "3333 5556 3333 2222";
    private static final String MASKED_PAN = "**** **** **** 2222";
    private static final String MSISDN = "+38050413228*";
    private static final Currency CURRENCY = Currency.getInstance("EUR");
    private static final String STREET = "Buhaker str";
    private static final String BLD_NR = "13";
    private static final String CITY = "Nernberg";
    private static final String POSTAL_CODE = "04310";
    private static final String COUNTRY = "Germany";
    private static final BigDecimal AMOUNT = BigDecimal.TEN;

    @InjectMocks
    private PaymentMapper mapper = Mappers.getMapper(PaymentMapper.class);

    @Mock
    private TimeMapper timeMapper;

    @Test
    void mapToPayment() {
        // Given
        when(timeMapper.mapTime(any())).thenReturn(REQUESTED_TIME);
        SinglePaymentTO expected = getSinglePaymentTO();

        // When
        SinglePaymentTO result = mapper.toPayment(getCmsSinglePayment());

        // Then
        assertEquals(expected, result);
    }

    @Test
    void mapPisExecutionRule() {
        // When
        String rule = mapper.mapPisExecutionRule(PisExecutionRule.FOLLOWING);

        // Then
        assertFalse(StringUtils.isEmpty(rule));
    }

    @Test
    void mapPisExecutionRule_null() {
        // When
        String rule = mapper.mapPisExecutionRule(null);

        // Then
        assertTrue(StringUtils.isEmpty(rule));
    }

    @Test
    void mapPisDayOfExecution() {
        // When
        int day = mapper.mapPisDayOfExecution(PisDayOfExecution._3);

        // Then
        assertEquals(day, Integer.parseInt(PisDayOfExecution._3.getValue()));
    }

    @Test
    void mapPisDayOfExecution_null() {
        // When
        int day = mapper.mapPisDayOfExecution(null);

        // Then
        assertEquals(day, Integer.parseInt(PisDayOfExecution._1.getValue()));
    }

    private CmsSinglePayment getCmsSinglePayment() {
        CmsSinglePayment payment = new CmsSinglePayment(PaymentProductTO.INSTANT_SEPA.getValue());
        payment.setPaymentId(PAYMENT_ID);
        payment.setPaymentProduct(PaymentProductTO.INSTANT_SEPA.getValue());
        payment.setPsuIdDatas(new ArrayList<>());
        payment.setTppInfo(new TppInfo());
        payment.setCreationTimestamp(null);
        payment.setStatusChangeTimestamp(null);

        payment.setEndToEndIdentification(END_TO_EN_ID);
        payment.setDebtorAccount(getCmsAccountReference());
        payment.setInstructedAmount(getCmsAmount());
        payment.setCreditorAccount(getCmsAccountReference());
        payment.setCreditorAgent(CREDITOR_AGENT);
        payment.setCreditorName(CREDITOR_NAME);
        payment.setCreditorAddress(getCmsAddress());
        payment.setRemittanceInformationUnstructured(REMITTANCE);
        payment.setPaymentStatus(TransactionStatus.ACCP);
        payment.setRequestedExecutionDate(REQUESTED_DATE);
        payment.setRequestedExecutionTime(OffsetDateTime.of(REQUESTED_DATE, REQUESTED_TIME, ZoneOffset.UTC));
        payment.setUltimateDebtor(null);
        payment.setUltimateCreditor(null);
        payment.setRemittanceInformationStructured(null);
        return payment;
    }

    private CmsAddress getCmsAddress() {
        CmsAddress address = new CmsAddress();
        address.setStreet(STREET);
        address.setBuildingNumber(BLD_NR);
        address.setCity(CITY);
        address.setPostalCode(POSTAL_CODE);
        address.setCountry(COUNTRY);
        return address;
    }

    private CmsAmount getCmsAmount() {
        return new CmsAmount(CURRENCY, AMOUNT);
    }

    private AccountReference getCmsAccountReference() {
        return new AccountReference(null, null, IBAN, BBAN, PAN, MASKED_PAN, MSISDN, CURRENCY);
    }

    private SinglePaymentTO getSinglePaymentTO() {
        return new SinglePaymentTO(PAYMENT_ID, END_TO_EN_ID, getReferenceTO(), getAmountTO(), getReferenceTO(), CREDITOR_AGENT, CREDITOR_NAME, getAddressTO(), REMITTANCE, TransactionStatusTO.ACCP, PaymentProductTO.INSTANT_SEPA, REQUESTED_DATE, REQUESTED_TIME);
    }

    private AmountTO getAmountTO() {
        return new AmountTO(CURRENCY, AMOUNT);
    }

    private AddressTO getAddressTO() {
        return new AddressTO(STREET, BLD_NR, CITY, POSTAL_CODE, COUNTRY, null, null);
    }

    private AccountReferenceTO getReferenceTO() {
        return new AccountReferenceTO(IBAN, BBAN, PAN, MASKED_PAN, MSISDN, CURRENCY);
    }
}
