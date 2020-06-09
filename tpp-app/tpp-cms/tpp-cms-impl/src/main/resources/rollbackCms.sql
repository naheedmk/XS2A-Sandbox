-- consents:

with
     psuIds as
       (select distinct id from psu_data where psu_id in ?1),

     consentIds as
       (select distinct consent_id from consent where creation_timestamp > ?2),

     consentIdsFilteredByPsuIds as
       (select distinct consent_id from consent_psu_data where psu_data_id in
                                                               (select * from psuIds) and consent_id in (select * from consentIds)),

     deleteAccountAccess as
       (delete from account_access where consent_id in
                                         (select * from consentIdsFilteredByPsuIds)),
     deleteAisAccountAccess as
       (delete from ais_account_access where consent_id in
                                             (select * from consentIdsFilteredByPsuIds)),
     deleteAisAspspAccountAccess as
       (delete from ais_aspsp_account_access where consent_id in
                                                   (select * from consentIdsFilteredByPsuIds)),
     deleteAisConsent as
       (delete from ais_consent where id in
                                      (select * from consentIdsFilteredByPsuIds)),
     deleteAisConsentPsuData as
       (delete from ais_consent_psu_data where ais_consent_id in
                                               (select * from consentIdsFilteredByPsuIds)),
     deleteAisConsentTransaction as
       (delete from ais_consent_transaction where consent_id in
                                                  (select * from consentIdsFilteredByPsuIds)),
     deleteAisConsentUsage as
       (delete from ais_consent_usage where consent_id in
                                            (select * from consentIdsFilteredByPsuIds)),
     deleteAspspAccountAccess as
       (delete from aspsp_account_access where consent_id in
                                               (select * from consentIdsFilteredByPsuIds)),
     deleteAspspConsentData as
       (delete from aspsp_consent_data acd where acd.consent_id in
                                                 (select external_id from consent c where c.consent_id in (select * from consentIdsFilteredByPsuIds))),

     authorisationIds as
       (select authorisation_id from authorisation where parent_id in
                                                         (select external_id from consent c where c.consent_id in (select * from consentIdsFilteredByPsuIds))),

     deleteAuthAvailableScaMethod as
       (delete from auth_available_sca_method where authorisation_id in
                                                    (select * from authorisationIds)),
     deleteAuthorisation as
       (delete from authorisation where parent_id in
                                        (select external_id from consent c where c.consent_id in (select * from consentIdsFilteredByPsuIds))),
     deleteConsentPsuData as
       (delete from consent_psu_data where consent_id in
                                           (select * from consentIdsFilteredByPsuIds)),
     deleteConsentUsage as
       (delete from consent_usage where consent_id in
                                        (select * from consentIdsFilteredByPsuIds))

     delete from consent where consent_id in (select * from consentIdsFilteredByPsuIds);

-- payments:

with
  psuIds as
    (select distinct id from psu_data where psu_id in ?1),
  paymentIds as
    (select distinct id from pis_common_payment where creation_timestamp > ?2),

  paymentIdsFilteredByPsuIds as
    (select distinct pis_common_payment_id from pis_common_payment_psu_data where psu_data_id in
                                                            (select * from psuIds) and pis_common_payment_id in (select * from paymentIds)),
  pisPaymentDataIds as
    (select distinct id from pis_payment_data where common_payment_id in (select * from paymentIdsFilteredByPsuIds)),

  deletePisPaymentData as
    (delete from pis_payment_data where common_payment_id in
                                      (select * from pisPaymentDataIds)),
  deleteAspspConsentData as
    (delete from aspsp_consent_data acd where acd.consent_id in
                                              (select payment_id from pis_common_payment pcc where pcc.id in (select * from paymentIdsFilteredByPsuIds))),
  authorisationIds as
    (select authorisation_id from authorisation where parent_id in
                                                     (select payment_id from pis_common_payment pcc where pcc.id in (select * from paymentIdsFilteredByPsuIds))),

  deleteAuthAvailableScaMethod as
    (delete from auth_available_sca_method where authorisation_id in
                                                  (select * from authorisationIds)),
  deleteAuthorisation as
    (delete from authorisation where authorisation_id in
                                      (select * from authorisationIds)),
  deletePaymentPsuData as
    (delete from pis_common_payment_psu_data where pis_common_payment_id in
                                        (select * from paymentIdsFilteredByPsuIds))

delete from pis_common_payment where id in (select * from paymentIdsFilteredByPsuIds);
