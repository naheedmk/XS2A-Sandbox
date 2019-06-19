import {Component, DoCheck, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {Subscription} from "rxjs";

@Component({
    selector: 'app-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit, DoCheck {

    subscription = new Subscription();

    constructor(private authService: AuthService) {
    }

    ngOnInit() {
    }

    onLogout(): void {
        this.authService.logout();
    }

    ngDoCheck(): void {
        if (!this.authService.isLoggedIn()) {
            this.authService.logout();
            throw new Error('Session expired. Please login again.');
        }
    }
}
