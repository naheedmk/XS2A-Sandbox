package de.adorsys.ledgers.oba.service.impl.service.redirect;

import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.domain.redirect.RedirectContext;
import de.adorsys.ledgers.oba.service.api.service.redirect.CmsRedirectService;
import de.adorsys.ledgers.oba.service.api.service.redirect.LedgersAccountInformationService;
import de.adorsys.ledgers.oba.service.api.service.redirect.LedgersAuthorizationService;
import de.adorsys.ledgers.oba.service.api.service.redirect.RedirectCommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Slf4j
@Service
@SuppressWarnings("PMD.AvoidReassigningParameters")
@RequiredArgsConstructor
public class RedirectCommonServiceImpl implements RedirectCommonService {
    private final CmsRedirectService cmsRedirectService;
    private final LedgersAuthorizationService ledgersAuthorizationService;
    private final LedgersAccountInformationService ledgersAccountInformationService;

    @Override
    public RedirectContext loginForOperation(RedirectContext context, String login, String pin) {
        context = ledgersAuthorizationService.loginForConsent(login, pin, context);
        context.updateAccountsAndConsent(ledgersAccountInformationService::getAccounts);
        return context;
    }

    @Override
    public RedirectContext initOperation(RedirectContext context) {
        context = ledgersAuthorizationService.initOperation(context);
        return updateCms(cmsRedirectService::updateCmsDataWithAccess, context);
    }

    @Override
    public RedirectContext selectMethod(RedirectContext context) {
        context = ledgersAuthorizationService.selectMethod(context);
        return updateCms(cmsRedirectService::cmsStatusesUpdate, context);
    }

    @Override
    public RedirectContext authorizeOperation(RedirectContext context) {
        context = ledgersAuthorizationService.validateTan(context);
        return updateCms(cmsRedirectService::cmsStatusesUpdate, context);
    }

    @Override
    public RedirectContext failOperation(RedirectContext context) {
        context.failContext();
        return updateCms(cmsRedirectService::cmsStatusesUpdate, context);
    }

    private RedirectContext updateCms(Function<RedirectContext, Boolean> function, RedirectContext context) {
        if (!function.apply(context)) {
            throw new ObaException("Something went wrong during Cms update!", ObaErrorCode.CMS_UPDATE_ERROR);
        }
        return context;
    }
}
