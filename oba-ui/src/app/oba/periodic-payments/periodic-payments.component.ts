import {Component, OnInit} from '@angular/core';
import {ShareDataService} from "../../common/services/share-data.service";
import {Subscription} from "rxjs/index";
import {PaymentAuthorizeResponse} from "../../api/models/payment-authorize-response";
import {PaymentTO} from "../../api/models/payment-to";
import {OnlineBankingService} from "../../common/services/online-banking.service";
import {InfoService} from "../../common/info/info.service";
import {PaymentTargetTO} from "../../api/models/payment-target-to";

@Component({
    selector: 'app-periodic-payments',
    templateUrl: './periodic-payments.component.html',
    styleUrls: ['./periodic-payments.component.scss']
})
export class PeriodicPaymentsComponent implements OnInit {

    payments: PaymentTO[];
    paymentsTarget: PaymentTargetTO[];
    private subscriptions: Subscription[] = [];

    constructor(private infoService: InfoService,
                private onlineBankingService: OnlineBankingService ) {
    }

    ngOnInit() {
        this.getPeriodicPayments();
    }

    getPeriodicPayments() {
        this.onlineBankingService.getPayments().subscribe(payments => {
            this.payments = payments;

            for (let target of this.payments) {
                this.paymentsTarget = target.targets;
            }
        });
    }
}
