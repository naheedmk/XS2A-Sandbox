import {Component, OnInit} from '@angular/core';
import {ShareDataService} from "../../common/services/share-data.service";
import {Subscription} from "rxjs/index";
import {PaymentAuthorizeResponse} from "../../api/models/payment-authorize-response";

@Component({
    selector: 'app-periodic-payments',
    templateUrl: './periodic-payments.component.html',
    styleUrls: ['./periodic-payments.component.scss']
})
export class PeriodicPaymentsComponent implements OnInit {

    payments;
    public authResponse: PaymentAuthorizeResponse;
    private subscriptions: Subscription[] = [];

    constructor(private sharedService: ShareDataService) {
    }

    ngOnInit() {
        this.sharedService.currentData.subscribe(
            authResponse => this.authResponse = authResponse
        );

        this.getPeriodicPayments();
    }

    getPeriodicPayments() {
        if (!this.authResponse || !this.authResponse.payment) {
            return;
        } else {
            this.payments = this.authResponse.payment;
        }
    }
}
