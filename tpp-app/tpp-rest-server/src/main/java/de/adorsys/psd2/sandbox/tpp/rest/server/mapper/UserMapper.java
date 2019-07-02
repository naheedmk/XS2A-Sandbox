package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    UserTO userToUserTO(User user);

    User userTOtoUser(UserTO userTO);
}
