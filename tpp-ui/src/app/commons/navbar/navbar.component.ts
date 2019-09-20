import {Component, DoCheck, OnInit} from '@angular/core';

import { AuthService } from '../../services/auth.service';
import { CustomizeService } from '../../services/customize.service';
import {UserInfoService} from "../../services/user-info.service";
import {UserInfo} from "../../models/userInfo.model";

@Component({
    selector: 'app-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements DoCheck, OnInit {

    public userInfo: UserInfo;
    public openDropdownMenu: boolean = false;

    constructor(private authService: AuthService,
                private userInfoService: UserInfoService,
                public customizeService: CustomizeService) {}

    ngDoCheck(): void {
        if (!this.authService.isLoggedIn()) {
            this.authService.logout();
            throw new Error('Session expired. Please login again.');
        }
    }

    ngOnInit(): void {
        if (this.authService.isLoggedIn()) {
            this.userInfoService.loadUserInfo().subscribe(
                (response: any) => {
                    this.userInfo = {
                        username: response.login,
                        email: response.email,
                        tppId: response.id
                    }
                }
            );
        }

    }

    onLogout(): void {
        this.authService.logout();
    }

    handleDropdownMenu(): void {
        this.openDropdownMenu = !this.openDropdownMenu;
    }
}
