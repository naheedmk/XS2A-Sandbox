package de.adorsys.ledgers.oba.rest.api.exception;

import org.springframework.http.ResponseEntity;

import de.adorsys.ledgers.oba.rest.api.domain.ConsentAuthorisationResponse;

public class ConsentAuthorisationException extends Exception {
	private static final long serialVersionUID = 7876974990567439886L;
	private final ResponseEntity<ConsentAuthorisationResponse> error;

	public ConsentAuthorisationException(ResponseEntity<ConsentAuthorisationResponse> error) {
		this.error = error;
	}

	public ResponseEntity<ConsentAuthorisationResponse> getError() {
		return error;
	}
}
