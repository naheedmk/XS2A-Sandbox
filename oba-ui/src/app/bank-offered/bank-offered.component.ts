import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AccountDetailsTO} from '../../../api/models/account-details-to';
import {RoutingPath} from '../common/models/routing-path.model';
import {AisService} from '../common/services/ais.service';
import {ShareDataService} from '../common/services/share-data.service';
import {ObaUtils} from '../common/utils/oba-utils';
import {ConsentAuthorizeResponse} from 'api/models';
import {Subscription} from 'rxjs';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-bank-offered',
  templateUrl: './bank-offered.component.html',
  styleUrls: ['./bank-offered.component.scss']
})
export class BankOfferedComponent implements OnInit {

  private subscriptions: Subscription[] = [];
  public authResponse: ConsentAuthorizeResponse;
  public bankOfferedForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private aisService: AisService,
    private shareService: ShareDataService) {
    this.bankOfferedForm = this.formBuilder.group({});
  }

  public ngOnInit(): void {
    this.shareService.currentData.subscribe(data => {
      if (data) {
        this.shareService.currentData.subscribe(authResponse => this.authResponse = authResponse);
      }
    });
  }

  public onSubmit(): void {
    if (!this.authResponse) {
      console.log('Missing application data');
      return;
    }
    this.subscriptions.push(
      this.aisService.startConsentAuth({
        encryptedConsentId: this.authResponse.encryptedConsentId,
        authorisationId: this.authResponse.authorisationId,
        aisConsent: this.authResponse.consent,
      }).subscribe(authResponse => {
        this.authResponse = authResponse;
        this.shareService.changeData(this.authResponse);
        this.router.navigate([`${RoutingPath.SELECT_SCA}`],
          ObaUtils.getQueryParams(this.authResponse.encryptedConsentId, this.authResponse.authorisationId));
      })
    );
  }

  public cancel(): void {
    this.aisService.revokeConsent({
      encryptedConsentId: this.authResponse.encryptedConsentId,
      authorisationId: this.authResponse.authorisationId
    }).subscribe(authResponse => {
      console.log(authResponse);
      this.authResponse = authResponse;
      this.shareService.changeData(this.authResponse);
      this.router.navigate([`${RoutingPath.RESULT}`],
        ObaUtils.getQueryParams(this.authResponse.encryptedConsentId, this.authResponse.authorisationId));
    });
  }

  handleObjectSelectedEvent(value, container): void {
    const idx = container.indexOf(value);
    if (idx > -1) { // is currently selected
      container.splice(idx, 1);
    } else { // is newly selected
      container.push(value);
    }
  }

  public accountsChecked(account): boolean {
    return this.authResponse.consent.access.accounts.indexOf(account.iban) > -1
  }

  public balancesChecked(account): boolean {
    return this.authResponse.consent.access.balances.indexOf(account.iban) > -1;
  }

  public transactionsChecked(account): boolean {
    return this.authResponse.consent.access.transactions.indexOf(account.iban) > -1;
  }

  get accounts(): Array<AccountDetailsTO> {
    return this.authResponse ? this.authResponse.accounts : [];
  }

  get consentAccounts(): Array<string> {
    return this.authResponse.consent.access.accounts;
  }

  get consentBalances(): Array<string> {
    return this.authResponse.consent.access.balances;
  }

  get consentTransactions(): Array<string> {
    return this.authResponse.consent.access.transactions;
  }

}
