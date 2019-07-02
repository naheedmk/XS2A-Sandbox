package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.TppInfo;
import org.mapstruct.Mapper;

@Mapper
public interface TppInfoMapper {

    UserTO ttpInfoToUserTO(TppInfo tppInfo);
}
