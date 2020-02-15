package de.adorsys.ledgers.oba.rest.server.resource.exception.resolver;

import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import org.springframework.http.HttpStatus;

import java.util.EnumMap;
import java.util.Map;

import static de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode.*;

public class AisExceptionStatusResolver {
    private static final Map<ObaErrorCode, HttpStatus> container = new EnumMap<>(ObaErrorCode.class);

    static {
        container.put(NOT_FOUND,HttpStatus.NOT_FOUND);
        //400 Block
        container.put(AIS_BAD_REQUEST, HttpStatus.BAD_REQUEST);
        container.put(AUTH_EXPIRED,HttpStatus.BAD_REQUEST);

        //500 Block
        container.put(CONNECTION_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        container.put(CONVERSION_EXCEPTION,HttpStatus.INTERNAL_SERVER_ERROR);
        container.put(CMS_UPDATE_ERROR,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private AisExceptionStatusResolver() {
    }

    public static HttpStatus resolveHttpStatusByCode(ObaErrorCode code) {
        return container.get(code);
    }
}
