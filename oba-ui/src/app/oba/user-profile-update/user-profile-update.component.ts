import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {OnlineBankingService} from '../../common/services/online-banking.service';
import {User} from '../../../../../tpp-ui/src/app/models/user.model';
import {Router} from '@angular/router';

@Component({
  selector: 'app-user-profile-edit',
  templateUrl: './user-profile-update.component.html',
  styleUrls: ['./user-profile-update.component.scss']
})
export class UserProfileUpdateComponent implements OnInit {
  public obaUser;
  public submitted;
  private userForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private onlineBankingService: OnlineBankingService,
    private router: Router) {
  }

  ngOnInit() {
    this.setDefaultUserDetails();
    this.setupEditUserFormControl();
  }

  get formControl() {
    return this.userForm.controls;
  }

  public onSubmit() {
    this.submitted = false;

    if (this.userForm.invalid) {
      return;
    }
    const updatedUser: User = {
      ...this.obaUser,
      username: this.userForm.get('username').value,
      email: this.userForm.get('email').value,
      pin: this.userForm.get('pin').value
    };
  }

  private setupEditUserFormControl(): void {
    this.userForm = this.formBuilder.group({
      username: ['', Validators.required],
      email: ['', [Validators.email, Validators.required]],
      pin: ['', [Validators.required]],
    });
  }

  private setDefaultUserDetails() {
    this.onlineBankingService.getCurrentUser()
      .subscribe((data) => {
        this.obaUser = data.body;
        this.userForm.setValue({
          username: this.obaUser.login,
          email: this.obaUser.email,
          pin: this.obaUser.pin
        });
      });
  }
}
