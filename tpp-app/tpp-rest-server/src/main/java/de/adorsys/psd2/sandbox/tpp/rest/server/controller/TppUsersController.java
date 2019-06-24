package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppUsersRestApi;
import org.springframework.http.ResponseEntity;

public class TppUsersController implements TppUsersRestApi {
    @Override
    public ResponseEntity<Void> createUser(UserTO account) {
        return null;
    }
}
