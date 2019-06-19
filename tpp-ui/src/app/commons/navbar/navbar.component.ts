import {Component, DoCheck, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {Subscription} from "rxjs";
import {AutoLogoutService} from "../../services/auto-logout.service";
import {InfoService} from "../info/info.service";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
    selector: 'app-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit, DoCheck {

    subscription = new Subscription();

    constructor(private authService: AuthService,
                private autoLogoutService: AutoLogoutService,
                private infoService: InfoService) {
    }

    ngOnInit() {
        // this.startTokenMonitoring();
    }

    // subscribe every minute and check if token is still valid
    startTokenMonitoring(): void {
        this.subscription = this.autoLogoutService.timerSubject.subscribe(time => {
            if (!this.authService.isLoggedIn()) {
                this.onLogout();
            }
        })
    }

    onLogout(): void {
        this.authService.logout();
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    ngDoCheck(): void {
        if (!this.authService.isLoggedIn()) {
            console.log("expired");
            this.authService.logout();
            throw new HttpErrorResponse({error: new Error("Session Expired"), status: 403, statusText: 'Your session expired'});

            // this.infoService.openFeedback("Your session has expired", {
            //     severity: 'error'
            // });

        }
    }
}
