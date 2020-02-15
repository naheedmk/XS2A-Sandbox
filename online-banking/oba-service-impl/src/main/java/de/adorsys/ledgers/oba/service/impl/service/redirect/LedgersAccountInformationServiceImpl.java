package de.adorsys.ledgers.oba.service.impl.service.redirect;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.client.rest.AccountRestClient;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.oba.service.api.domain.redirect.RedirectContext;
import de.adorsys.ledgers.oba.service.api.service.redirect.LedgersAccountInformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LedgersAccountInformationServiceImpl implements LedgersAccountInformationService {
    private final AccountRestClient accountRestClient;
    private final AuthRequestInterceptor authInterceptor;

    @Override
    public List<AccountDetailsTO> getAccounts(RedirectContext context) {
        authInterceptor.setAccessToken(context.getBearer());
        return accountRestClient.getListOfAccounts().getBody();
    }
}
