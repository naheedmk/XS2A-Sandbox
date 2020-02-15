package de.adorsys.ledgers.oba.service.api.service.redirect;

import de.adorsys.ledgers.oba.service.api.domain.redirect.RedirectContext;

public interface RedirectCommonService {

    RedirectContext loginForOperation(RedirectContext context, String login, String pin);

    RedirectContext initOperation(RedirectContext context);

    RedirectContext selectMethod(RedirectContext context);

    RedirectContext authorizeOperation(RedirectContext context);

    RedirectContext failOperation(RedirectContext context);
}
