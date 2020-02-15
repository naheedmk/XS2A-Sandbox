package de.adorsys.ledgers.oba.rest.api.resource.redirect;

import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.oba.service.api.domain.redirect.RedirectContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(value = RedirectApi.BASE_PATH, tags = "PSU REDIRECT CONTROLLERS. Provides access to online banking redirect functionality")
public interface RedirectApi {
    String BASE_PATH = "/api/v1/redirect/";

    @PostMapping(path = "/{encryptedOperationId}/authorisation/{authorisationId}/login")
    @ApiOperation(value = "Identifies the user by login an pin.")
    ResponseEntity<RedirectContext> loginForOperation(
        @PathVariable("encryptedOperationId") String encryptedOperationId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestParam(value = "operationType") OpTypeTO opType,
        @RequestParam(value = "login", required = false) String login,
        @RequestParam(value = "pin", required = false) String pin);

    @PostMapping("/{encryptedOperationId}/authorisation/{authorisationId}/start")
    @ApiOperation(value = "Initiate authorization", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<RedirectContext> init(
        @PathVariable("encryptedOperationId") String encryptedOperationId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestParam(value = "operationType") OpTypeTO opType,
        @RequestBody Object operationObject);

    @PostMapping("/{encryptedOperationId}/authorisation/{authorisationId}/methods/{scaMethodId}")
    @ApiOperation(value = "Selects the SCA Method for use.", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<RedirectContext> selectMethod(
        @PathVariable("encryptedOperationId") String encryptedOperationId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestParam(value = "operationType") OpTypeTO opType,
        @PathVariable("scaMethodId") String scaMethodId);

    @PostMapping(path = "/{encryptedOperationId}/authorisation/{authorisationId}/authCode", params = {"authCode"})
    @ApiOperation(value = "Validates TAN for authorization", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<RedirectContext> authorizeOperation(
        @PathVariable("encryptedOperationId") String encryptedOperationId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestParam(value = "operationType") OpTypeTO opType,
        @RequestParam("authCode") String authCode);

    @DeleteMapping(path = "/{encryptedOperationId}/{authorisationId}")
    @ApiOperation(value = "Revoke procedure", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<RedirectContext> revoke(
        @PathVariable("encryptedOperationId") String encryptedOperationId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestParam(value = "operationType") OpTypeTO opType);

//TODO Fix this!

   /* @PostMapping(path = "/piis")
    @ApiOperation(value = "Grant a piis consent", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<PIISConsentCreateResponse> grantPiisConsent(
        @RequestHeader(name = "Cookie", required = false) String consentAndaccessTokenCookieString, @RequestBody CreatePiisConsentRequestTO piisConsentTO);*/

}

