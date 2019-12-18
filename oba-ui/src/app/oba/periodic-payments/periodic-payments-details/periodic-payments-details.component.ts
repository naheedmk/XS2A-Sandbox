import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {PeriodicPaymentTO} from "../../../api/models/periodic-payment-to";
import {map} from "rxjs/operators";

@Component({
    selector: 'app-periodic-payments-details',
    templateUrl: './periodic-payments-details.component.html',
    styleUrls: ['./periodic-payments-details.component.scss']
})
export class PeriodicPaymentsDetailsComponent implements OnInit {

    payment: PeriodicPaymentTO;
    paymentID: string;

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute) {
    }

    ngOnInit() {
        this.activatedRoute.params.pipe(
            map(resp => resp.id)
        ).subscribe((paymentID: string) => {
                this.paymentID = paymentID;
                this.getPaymentsDetail();
            }
        );
    }

    getPaymentsDetail() {
    }

}
