package de.adorsys.ledgers.oba.service.api.service.redirect;

import de.adorsys.ledgers.oba.service.api.domain.redirect.RedirectContext;

public interface LedgersAuthorizationService {
    RedirectContext loginForConsent(String login, String pin, RedirectContext context);

    RedirectContext initOperation(RedirectContext context);

    RedirectContext selectMethod(RedirectContext context);

    RedirectContext validateTan(RedirectContext context);
}
