package de.adorsys.ledgers.oba.rest.api.consentref;

import lombok.Data;

@Data
public class ConsentReference {
	
	private String authorisationId;
	private String redirectId;
	private ConsentType consentType;
	private String encryptedConsentId;
	private String cookieString;

}
