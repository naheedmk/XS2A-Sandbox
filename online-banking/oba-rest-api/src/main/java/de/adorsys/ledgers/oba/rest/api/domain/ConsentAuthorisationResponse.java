package de.adorsys.ledgers.oba.rest.api.domain;

import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsentAuthorisationResponse extends AuthorisationResponse {
	private AisConsentTO consent;
}
