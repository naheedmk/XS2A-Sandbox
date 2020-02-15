package de.adorsys.ledgers.oba.service.api.service.redirect;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.oba.service.api.domain.redirect.RedirectContext;

import java.util.List;

public interface LedgersAccountInformationService {
    List<AccountDetailsTO> getAccounts(RedirectContext context);
}
