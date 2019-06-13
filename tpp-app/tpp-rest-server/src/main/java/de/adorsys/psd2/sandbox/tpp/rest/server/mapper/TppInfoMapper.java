package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.TppInfo;
import org.springframework.stereotype.Component;

// TODO Use map struct instead
@Component
public class TppInfoMapper {

    public UserTO fromTppInfo(TppInfo info) {
        return new UserTO(info.getLogin(), info.getEmail(), info.getPin());
    }
}
