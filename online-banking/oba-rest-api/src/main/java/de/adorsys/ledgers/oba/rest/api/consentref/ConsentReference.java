package de.adorsys.ledgers.oba.rest.api.consentref;

import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import lombok.Data;

@Data
public class ConsentReference {
    private String authorizationId;
    private String redirectId;
    private ConsentType consentType;
    private String encryptedConsentId;
    private ScaStatusTO status;
    private String cookieString;
}
