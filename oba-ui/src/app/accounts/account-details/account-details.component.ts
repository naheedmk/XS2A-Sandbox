import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {OnlineBankingService} from "../../common/services/online-banking.service";
import {AccountDetailsTO, TransactionTO} from "../../api/models";
import {OnlineBankingAccountInformationService} from "../../api/services/online-banking-account-information.service";
import {map} from "rxjs/operators";
import {NgbDateStruct} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";


@Component({
    selector: 'app-account-details',
    templateUrl: './account-details.component.html',
    styleUrls: ['./account-details.component.scss']
})
export class AccountDetailsComponent implements OnInit {

    account: AccountDetailsTO;
    accountID: string;
    transactions: TransactionTO[];
    model1: NgbDateStruct = {year: 2019, month: 2, day:23};
    model2: NgbDateStruct = {year: 2019, month: 12, day:23};
    timeForm: FormGroup;
    transactionsParams: OnlineBankingAccountInformationService.TransactionsUsingGETParams = {
        accountId: '',
        dateFrom: '',
        dateTo: ''
    };

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private onlineBankingService: OnlineBankingService,
                public formBuilder: FormBuilder) {
    }

    ngOnInit() {
        this.activatedRoute.params.pipe(
            map(resp => resp.id)
        ).subscribe((accountID: string) => {
                this.accountID = accountID;
                this.initDate(accountID);
                this.getTransactions();
                this.getAccountDetail();
            }
        );
        this.timeForm = this.formBuilder.group(
            {
                datePicker: [null, Validators.required],
            }
        );
    }

    getAccountDetail() {
        this.onlineBankingService.getAccount(this.accountID)
            .subscribe((account: AccountDetailsTO) => this.account = account);
    }

    getTransactions() {
        this.onlineBankingService.getTransactions(this.transactionsParams)
            .subscribe((transactions: TransactionTO[]) => this.transactions = transactions)
    }

    initDate(id: string) {
        this.transactionsParams = {
           accountId: id,
           dateFrom: new Date(this.model1.year, this.model1.month, this.model1.day).toISOString().split("T")[0],
           dateTo: new Date(this.model2.year, this.model2.month, this.model2.day).toISOString().split("T")[0]
        }
    }
}
