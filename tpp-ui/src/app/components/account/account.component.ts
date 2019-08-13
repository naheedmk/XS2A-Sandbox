import {Component, OnInit} from '@angular/core';
import {map} from "rxjs/operators";
import {AccountService} from "../../services/account.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Account} from "../../models/account.model";
import {InfoService} from "../../commons/info/info.service";

@Component({
    selector: 'app-account',
    templateUrl: './account.component.html',
    styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {

    public account: Account;
    accountID: string;

    constructor(
        private accountService: AccountService,
        private activatedRoute: ActivatedRoute,
        public infoService: InfoService,
        private router: Router) {
    }

    ngOnInit() {
        this.activatedRoute.params
            .pipe(
                map(response => {
                    return response.id;
                })
            )
            .subscribe((accountID: string) => {
                this.accountID = accountID;
                this.getAccount();
            });
    }

    public isAccountEnabled(account: Account): boolean {
        return account.accountStatus !== "DELETED" && account.accountStatus !== "BLOCKED";
    }

    public isAccountDeleted(account: Account): boolean {
        if (account.accountStatus === "DELETED") {
            this.infoService.openFeedback('You can not Grant Accesses to a Deleted/Blocked account', {
                severity: 'error'
            });
            return false;
        }
        this.router.navigate(['/accounts/' + account.id + '/access']);
        return true;
    }

    getAccount() {
        this.accountService.getAccount(this.accountID)
            .subscribe((account: Account) => {
                this.account = account;
            })
    }
}
