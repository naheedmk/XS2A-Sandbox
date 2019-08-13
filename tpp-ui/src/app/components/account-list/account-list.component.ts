import {Component, OnDestroy, OnInit} from '@angular/core';
import {AccountService} from "../../services/account.service";

import {Account} from "../../models/account.model"
import {Subscription} from "rxjs";
import {InfoService} from "../../commons/info/info.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-account-list',
    templateUrl: './account-list.component.html',
    styleUrls: ['./account-list.component.scss']
})
export class AccountListComponent implements OnInit, OnDestroy {
    accounts: Account[];
    subscription = new Subscription();

    constructor(private accountService: AccountService,
                private infoService: InfoService,
                private router: Router) {
    }

    ngOnInit() {
        this.getAccounts();
    }

    getAccounts(): void {
        this.subscription.add(
            this.accountService.getAccounts()
            .subscribe((accounts: Account[]) => {
                this.accounts = accounts;
            }));

    }

    public isAccountDeleted(account: Account): boolean {
        if (account.accountStatus === "DELETED") {
            this.infoService.openFeedback('You can not Grant Accesses to a Deleted/Blocked account', {
                severity: 'error'
            });
            return false;
        }
        this.router.navigate([`/accounts/`, account.id]);
        return true;
    }

    public isAccountEnabled(account: Account): boolean {
       return (account.accountStatus !== "DELETED" && account.accountStatus !== "BLOCKED");
    }


    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

}
