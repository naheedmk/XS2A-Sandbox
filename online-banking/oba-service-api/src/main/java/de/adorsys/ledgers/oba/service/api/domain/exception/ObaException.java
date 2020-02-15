package de.adorsys.ledgers.oba.service.api.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class ObaException extends RuntimeException {
    private final String devMessage;
    private final ObaErrorCode obaErrorCode;
}
