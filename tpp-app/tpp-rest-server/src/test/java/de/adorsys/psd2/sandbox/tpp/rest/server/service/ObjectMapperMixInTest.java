package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.um.ScaMethodTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.ScaUserDataMixedIn;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectMapperMixInTest {

    @Test
    public void scaUserDataMixIn() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixIn(ScaUserDataTO.class, ScaUserDataMixedIn.class);

        UserTO user = getUser();
        String s = objectMapper.writeValueAsString(user);
        System.out.println(s);
        assertThat(s.contains("true")).isTrue();
        assertThat(s.contains("STATIC TAN")).isTrue();
    }

    private UserTO getUser() {
        return new UserTO("id", "login", "email", "pin", getScaUserData(), Collections.emptyList(), Collections.emptyList(), "branch");
    }

    private List<ScaUserDataTO> getScaUserData() {
        return Collections.singletonList(new ScaUserDataTO("id", ScaMethodTypeTO.EMAIL, "methodValue", null, true, "STATIC TAN"));
    }
}
