package de.adorsys.ledgers.oba.rest.api.domain;

import de.adorsys.ledgers.middleware.api.domain.payment.*;
import lombok.Value;

@Value
public class PaymentAuthorisationResponse extends AuthorisationResponse {
	private final SinglePaymentTO singlePayment;
	private final BulkPaymentTO bulkPayment;
	private final PeriodicPaymentTO periodicPayment;

	public PaymentAuthorisationResponse() {
		this.singlePayment = null;
		this.bulkPayment = null;
		this.periodicPayment = null;
	}

	public PaymentAuthorisationResponse(PaymentTypeTO paymentType, Object payment) {
		switch (paymentType) {
			case SINGLE:
				this.singlePayment = (SinglePaymentTO)payment;
				this.bulkPayment = null;
				this.periodicPayment = null;
				break;
			case BULK:
				this.singlePayment = null;
				this.bulkPayment = (BulkPaymentTO)payment;
				this.periodicPayment = null;
				break;
			default:
				this.singlePayment = null;
				this.bulkPayment = null;
				this.periodicPayment = (PeriodicPaymentTO)payment;
		}	
	}

	public void updatePaymentStatus(TransactionStatusTO paymentStatus) {
		if(singlePayment!=null) {
			singlePayment.setPaymentStatus(paymentStatus);
		} else if (bulkPayment!=null) {
			bulkPayment.setPaymentStatus(paymentStatus);
		} else if (periodicPayment!=null) {
			periodicPayment.setPaymentStatus(paymentStatus);
		}
	}
	
}
