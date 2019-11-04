import { Component, OnInit } from '@angular/core';
import { UserService } from "../../../services/user.service";
import { User } from "../../../models/user.model";
import { ActivatedRoute, Router } from "@angular/router";
import { AccountService } from '../../../services/account.service';

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.scss']
})
export class UserDetailsComponent implements OnInit {
  user: User;
  userId: string;

  constructor(private userService: UserService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private accService: AccountService) {
    this.user = new User();
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((param) => {
      this.userId = param['id'];
      this.getUserById();
    });
  }

  getUserById() {
    this.userService.getUser(this.userId).subscribe(
      (user: User) => this.user = user
    );
  }

  handleClickOnIBAN(event) {
    var id = '';
    let iban = event.target.innerHTML;
    this.accService.getAllAccounts().subscribe(
      (accs) => {
        accs.forEach(element => {
          if (element.iban === iban) id = element.id;
        });
        console.log(id);
      }
    );
  }
}
