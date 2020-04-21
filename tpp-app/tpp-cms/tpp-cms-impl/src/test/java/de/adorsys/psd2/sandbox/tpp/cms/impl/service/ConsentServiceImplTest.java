package de.adorsys.psd2.sandbox.tpp.cms.impl.service;

import de.adorsys.psd2.consent.api.CmsResponse;
import de.adorsys.psd2.consent.api.WrongChecksumException;
import de.adorsys.psd2.consent.api.ais.CmsConsent;
import de.adorsys.psd2.consent.api.consent.CmsCreateConsentResponse;
import de.adorsys.psd2.consent.service.ConsentServiceInternal;
import de.adorsys.psd2.sandbox.tpp.cms.api.domain.AisConsent;
import de.adorsys.psd2.sandbox.tpp.cms.impl.mapper.ConsentMapper;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsentServiceImplTest {

    private static final String CONSENT_ID = "13456789";
    @InjectMocks
    private ConsentServiceImpl service;

    @Mock
    private ConsentServiceInternal consentServiceInternal;
    @Mock
    private ConsentMapper mapper;

    @Test
    void generateConsents() throws WrongChecksumException {
        // Given
        when(mapper.mapToCmsConsent(any())).thenReturn(new CmsConsent());
        when(consentServiceInternal.createConsent(any())).thenReturn(getCmsResponse());

        // When
        List<String> result = service.generateConsents(Collections.singletonList(consent()));

        // Then
        assertThat(result).isEqualTo(Collections.singletonList(CONSENT_ID));
    }

    private CmsResponse<CmsCreateConsentResponse> getCmsResponse() {
        CmsConsent consent = new CmsConsent();
        consent.setConsentStatus(ConsentStatus.VALID);
        return CmsResponse.<CmsCreateConsentResponse>builder()
                   .payload(new CmsCreateConsentResponse(CONSENT_ID, consent))
                   .build();
    }

    private AisConsent consent() {
        return new AisConsent();
    }
}
