package de.adorsys.ledgers.oba.rest.server.config;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.adorsys.ledgers.oba.rest.server.mapper.CmsSinglePaymentDeserializer;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import de.adorsys.ledgers.oba.rest.server.mapper.CmsPaymentDeserializer;
import de.adorsys.psd2.consent.api.pis.CmsPayment;

@Configuration
public class ObjectMapperConfig {

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void postConstruct() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(CmsPayment.class, new CmsPaymentDeserializer(objectMapper))
            .addDeserializer(CmsSinglePayment.class, new CmsSinglePaymentDeserializer(objectMapper));
        objectMapper.registerModule(module)
            .registerModule(new JavaTimeModule());
    }
}
