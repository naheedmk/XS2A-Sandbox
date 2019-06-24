package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppUsersRestApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(TppUsersRestApi.BASE_PATH)
public class TppUsersController implements TppUsersRestApi {

    private final UserMgmtStaffRestClient userMgmtStaffRestClient;

    @Override
    public ResponseEntity<UserTO> createUser(UserTO user) {
        return userMgmtStaffRestClient.createUser(user);
    }
}
