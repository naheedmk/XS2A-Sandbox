package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.oauth.OauthServerInfoTO;
import de.adorsys.ledgers.middleware.client.rest.OauthRestClient;
import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaOauthApi;
import de.adorsys.ledgers.oba.rest.server.service.OauthServerLinkResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ObaOauthApi.BASE_PATH)
public class ObaOauthController implements ObaOauthApi {
    private final OauthRestClient oauthRestClient;

    @Override
    public void oauthCode(String login, String pin, String redirectUi) {
    }

    @Override
    public void oauthToken(String code) {
    }

    @Override
    public ResponseEntity<OauthServerInfoTO> oauthServerInfo(String redirectId, String paymentId, String consentId, String cancellationId) {
        OauthServerInfoTO serverInfo = new OauthServerLinkResolver(oauthRestClient.oauthServerInfo().getBody(), redirectId, paymentId, consentId, cancellationId).resolve();
        return ResponseEntity.ok(serverInfo);
    }
}
