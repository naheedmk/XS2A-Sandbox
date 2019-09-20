import { Component, OnInit } from '@angular/core';
import {UserInfo} from "../../models/userInfo.model";
import {AuthService} from "../../services/auth.service";
import {UserInfoService} from "../../services/user-info.service";

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {

  public userInfo: UserInfo;

  constructor(private authService: AuthService,
      private userInfoService: UserInfoService) { }

    ngOnInit(): void {
      if (this.authService.isLoggedIn()) {
          this.userInfoService.loadUserInfo().subscribe(
              (response: any) => {
                  this.userInfo = {
                      username: response.login,
                      email: response.email,
                      tppId: response.id
                  };
                  console.log(this.userInfo);
              }
          );
      }
    }
}
