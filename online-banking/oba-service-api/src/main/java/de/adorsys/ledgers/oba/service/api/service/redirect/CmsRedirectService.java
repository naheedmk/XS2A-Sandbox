package de.adorsys.ledgers.oba.service.api.service.redirect;

import de.adorsys.ledgers.oba.service.api.domain.redirect.RedirectContext;

public interface CmsRedirectService {

    void getCmsObject(RedirectContext context);

    boolean updateCmsDataWithAccess(RedirectContext context);

    boolean cmsStatusesUpdate(RedirectContext context);
}
