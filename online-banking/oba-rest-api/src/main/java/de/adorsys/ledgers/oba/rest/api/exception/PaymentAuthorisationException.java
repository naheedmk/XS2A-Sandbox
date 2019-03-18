package de.adorsys.ledgers.oba.rest.api.exception;

import org.springframework.http.ResponseEntity;

import de.adorsys.ledgers.oba.rest.api.domain.PaymentAuthorisationResponse;

public class PaymentAuthorisationException extends Exception {
	private static final long serialVersionUID = 5719983070625127158L;
	private final ResponseEntity<PaymentAuthorisationResponse> error;

	public PaymentAuthorisationException(ResponseEntity<PaymentAuthorisationResponse> error) {
		this.error = error;
	}

	public ResponseEntity<PaymentAuthorisationResponse> getError() {
		return error;
	}
}
