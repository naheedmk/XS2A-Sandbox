import { Component, OnInit } from '@angular/core';
import { User } from '../../../../../tpp-ui/src/app/models/user.model';
import { OnlineBankingService } from '../../common/services/online-banking.service';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {
  public obaUser;

  constructor( private onlineBankingService: OnlineBankingService) { }

  ngOnInit() {
    this.getUserInfo();
  }

  private getUserInfo() {
    this.onlineBankingService.getCurrentUser().subscribe(data => this.obaUser = data.body);
   }

}
