import {Component, OnDestroy, OnInit} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {AccountService} from '../../services/account.service';
import {Router} from '@angular/router';
import {Account} from '../../models/account.model';
import {Subscription} from 'rxjs';
import {PageConfig, PaginationConfigModel} from "../../models/pagination-config.model";
import { debounceTime, tap } from 'rxjs/operators';

@Component({
  selector: 'app-account-list',
  templateUrl: './account-list.component.html',
  styleUrls: ['./account-list.component.scss']
})
export class AccountListComponent implements OnInit, OnDestroy {
  accounts: Account[] = [];
  subscription = new Subscription();
  searchForm: FormGroup;

  config: PaginationConfigModel = {
    itemsPerPage: 10,
    currentPageNumber: 1,
    totalItems: 0,
  };

  constructor(
    private accountService: AccountService,
    public router: Router,
    private formbuilder: FormBuilder) {}

  ngOnInit() {
    this.searchForm = this.formbuilder.group({
      query: ['', Validators.required],
      itemsPerPage: [this.config.itemsPerPage, Validators.required]
    });
    this.listAccounts(this.config.currentPageNumber, this.config.itemsPerPage);
    this.onQueryUsers();
  }

  listAccounts(page: number, size: number, queryParam: string = '') {
    this.accountService.listAccounts(page - 1, size, queryParam.toUpperCase()).subscribe(response => {
      this.accounts = response.accounts;
      this.config.totalItems = response.totalElements;
    });
  }

  onQueryUsers() {
    this.searchForm.valueChanges.pipe(
      tap(val => {
        this.searchForm.patchValue(val, { emitEvent: false });
      }),
      debounceTime(750)
    ).subscribe(form => {
        this.config.itemsPerPage = form.itemsPerPage;
        this.listAccounts(1, this.config.itemsPerPage, form.query);
      });
  }

  goToDepositCash(account: Account) {
    if (!this.isAccountEnabled(account)) { return false; }
    this.router.navigate(['/accounts/' + account.id + '/deposit-cash']);
  }

  isAccountEnabled(account: Account): boolean {
    return account.accountStatus !== 'DELETED';
  }

  pageChange(pageConfig: PageConfig) {
    this.listAccounts(pageConfig.pageNumber, pageConfig.pageSize, this.searchForm.get('query').value);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
