package de.adorsys.ledgers.oba.rest.api.domain;

import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import lombok.Data;

import java.util.List;

@Data
public class AuthorisationResponse extends OnlineBankingResponse  {

	private String encryptedConsentId;
	private List<ScaUserDataTO> scaMethods;
	private String authorisationId;
	private ScaStatusTO scaStatus;

}
