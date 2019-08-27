package de.adorsys.psd2.sandbox.tpp.rest.api.domain;

import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class User {
    private String id;
    private String email;
    private String login;
    private String pin;
    private List<ScaUserData> scaUserData;
    private List<UserRole> userRoles;
    private List<AccountAccess> accountAccesses = new ArrayList();
}
