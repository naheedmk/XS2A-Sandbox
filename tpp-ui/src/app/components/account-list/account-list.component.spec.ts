import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IconModule } from '../../commons/icon/icon.module';
import { InfoModule } from '../../commons/info/info.module';
import { InfoService } from '../../commons/info/info.service';
import { Account, AccountStatus, AccountType, UsageType } from '../../models/account.model';
import { AccountService } from '../../services/account.service';
import { AccountListComponent } from './account-list.component';
import { NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FilterPipeModule } from 'ngx-filter-pipe';
import { PaginationContainerComponent } from '../../commons/pagination-container/pagination-container.component';

describe('AccountListComponent', () => {
  let component: AccountListComponent;
  let fixture: ComponentFixture<AccountListComponent>;
  let accountService: AccountService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        HttpClientTestingModule,
        InfoModule,
        RouterTestingModule,
        FilterPipeModule,
        IconModule,
        NgbPaginationModule,
        FormsModule,
      ],
      declarations: [AccountListComponent, PaginationContainerComponent],
      providers: [AccountService, InfoService],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    accountService = TestBed.get(AccountService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load accounts on NgOnInit', () => {
    const mockAccounts: Account[] = [
      {
        id: 'XXXXXX',
        iban: 'DE35653635635663',
        bban: 'BBBAN',
        pan: 'pan',
        maskedPan: 'maskedPan',
        currency: 'EUR',
        msisdn: 'MSISDN',
        name: 'Pupkin',
        product: 'Deposit',
        accountType: AccountType.CASH,
        accountStatus: AccountStatus.ENABLED,
        bic: 'BIChgdgd',
        usageType: UsageType.PRIV,
        details: '',
        linkedAccounts: '',
        balances: [],
      } as Account,
    ];
    const getAccountsSpy = spyOn(accountService, 'getAccounts').and.returnValue(
      of({
        accounts: mockAccounts,
        totalElements: mockAccounts.length,
      })
    );

    component.ngOnInit();

    expect(getAccountsSpy).toHaveBeenCalled();
    expect(component.accounts).toEqual(mockAccounts);
  });

  it('should change the page', () => {
    const pageNumber = 10;
    const pageSize = 5;
    const mockPageConfig = {
      pageNumber,
      pageSize,
    };
    component.searchForm.setValue({
      query: 'foo',
      itemsPerPage: 15,
    });
    const getAccountsSpy = spyOn(component, 'getAccounts');
    component.pageChange(mockPageConfig);
    expect(getAccountsSpy).toHaveBeenCalledWith(pageNumber, pageSize, 'foo');
  });

  it('should load accounts', () => {
    const page = 5;
    const size = 10;
    const mockAccounts: Account[] = [
      {
        id: 'XXXXXX',
        iban: 'DE35653635635663',
        bban: 'BBBAN',
        pan: 'pan',
        maskedPan: 'maskedPan',
        currency: 'EUR',
        msisdn: 'MSISDN',
        name: 'Pupkin',
        product: 'Deposit',
        accountType: AccountType.CASH,
        accountStatus: AccountStatus.ENABLED,
        bic: 'BIChgdgd',
        usageType: UsageType.PRIV,
        details: '',
        linkedAccounts: '',
        balances: [],
      } as Account,
    ];
    const getAccountsSpy = spyOn(accountService, 'getAccounts').and.returnValue(
      of({ accounts: mockAccounts, totalElements: mockAccounts.length })
    );
    component.getAccounts(page, size);
    expect(getAccountsSpy).toHaveBeenCalled();
    expect(component.accounts).toEqual(mockAccounts);
    expect(component.config.totalItems).toEqual(mockAccounts.length);
  });

  it('should change the page size', () => {
    const pageSize = 10;
    component.config = {
      itemsPerPage: 0,
      currentPageNumber: 0,
      totalItems: 0,
    };
    component.changePageSize(pageSize);
    expect(component.config.itemsPerPage).toEqual(pageSize);
  });

  it('should return false if account is not set', () => {
    const mockAccount: Account = {
      id: '123456',
      iban: 'DE35653635635663',
      bban: 'BBBAN',
      pan: 'pan',
      maskedPan: 'maskedPan',
      currency: 'EUR',
      msisdn: 'MSISDN',
      name: 'Pupkin',
      product: 'Deposit',
      accountType: AccountType.CASH,
      accountStatus: AccountStatus.DELETED,
      bic: 'BIChgdgd',
      usageType: UsageType.PRIV,
      details: '',
      linkedAccounts: '',
      balances: [],
    } as Account;
    component.goToDepositCash(mockAccount);
  });
});
