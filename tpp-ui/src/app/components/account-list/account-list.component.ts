import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {debounceTime, tap} from 'rxjs/operators';
import {AccountService} from '../../services/account.service';
import {Router} from '@angular/router';
import {Account} from '../../models/account.model';
import {Subscription} from 'rxjs';


@Component({
  selector: 'app-account-list',
  templateUrl: './account-list.component.html',
  styleUrls: ['./account-list.component.scss']
})
export class AccountListComponent implements OnInit, OnDestroy {
  accounts: Account[];
  subscription = new Subscription();
  searchForm: FormGroup;
  config: { itemsPerPage, currentPage, totalItems, maxSize } = {
    itemsPerPage: 10,
    currentPage: 1,
    totalItems: 0,
    maxSize: 7
  };

  constructor(private accountService: AccountService,
              private formBuilder: FormBuilder,
              public router: Router) {
    this.accounts = [];
  }

  ngOnInit() {
    this.searchForm = this.formBuilder.group({
      query: ['', Validators.required],
      itemsPerPage: [this.config.itemsPerPage, Validators.required]
    });
    this.getAccounts(this.config.currentPage, this.config.itemsPerPage);

    this.onQueryAccounts();
  }

  getAccounts(page: number, size: number, queryParam: string = ''){
    this.accountService.getAccounts(page - 1, size, queryParam).subscribe(response => {
      this.accounts = response.accounts;
      this.config.totalItems = response.totalElements;
    });
  }

  goToDepositCash(account: Account) {
    if (!this.isAccountEnabled(account)) return false;
    this.router.navigate(['/accounts/' + account.id + '/deposit-cash']);
  }

  isAccountEnabled(account: Account): boolean {
    return (account.accountStatus !== "DELETED");
  }

  pageChange(pageNumber: number) {
    this.config.currentPage = pageNumber;
    this.getAccounts(pageNumber, this.config.itemsPerPage, this.searchForm.get('query').value);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  onQueryAccounts() {
    this.searchForm.valueChanges.pipe(
        tap(val => {
          this.searchForm.patchValue(val, {emitEvent: false});
        }),
        debounceTime(750)
    ).subscribe(form => {
      this.config.itemsPerPage = form.itemsPerPage;
      this.getAccounts(1, this.config.itemsPerPage, form.query);
    });
  }
}
