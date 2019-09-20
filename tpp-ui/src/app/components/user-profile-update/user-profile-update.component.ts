import { Component, OnInit } from '@angular/core';
import {UserService} from "../../services/user.service";
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ScaMethods} from "../../models/scaMethods";
import {User} from "../../models/user.model";
import {map} from "rxjs/operators";
import {ActivatedRoute, Router} from "@angular/router";
import {UserInfo} from "../../models/userInfo.model";
import {UserInfoService} from "../../services/user-info.service";

@Component({
  selector: 'app-user-profile-update',
  templateUrl: './user-profile-update.component.html',
  styleUrls: ['./user-profile-update.component.scss']
})
export class UserProfileUpdateComponent implements OnInit {

    userInfo: UserInfo;
    updateUserForm: FormGroup;
    methods: string[];

    userId: string;
    public submitted: boolean;
    public errorMessage: string;

    constructor(private userInfoService: UserInfoService,
                private formBuilder: FormBuilder,
                private router: Router,
                private activatedRoute: ActivatedRoute) {
    }

    ngOnInit() {
        this.setupUserFormControl();
        this.activatedRoute.params
            .pipe(
                map(response => {
                    return response.id;
                })
            )
            .subscribe((id: string) => {
                this.userId = id;
            });
    }

    setupUserFormControl(): void {
        this.updateUserForm = this.formBuilder.group({
            email: ['', [Validators.required, Validators.email]],
            login: ['', Validators.required],
            pin: ['', [Validators.required, Validators.minLength(5)]],
        });
    }

    get formControl() {
        return this.updateUserForm.controls;
    }
}
  /*  onSubmit() {
        this.submitted = true;
        if (this.updateUserForm.invalid) {
            this.errorMessage = 'Please verify your credentials';
            return;
        }

        const updatedUser: UserInfo = {
            ...this.userInfo,
            username: this.updateUserForm.get('login').value,
            tppId: this.updateUserForm.get('tppId').value,
            email: this.updateUserForm.get('email').value,
        };

        this.userService.updateUserDetails(updatedUser)
            .subscribe(() => this.router.navigate(['/users/all'])
            );
    }

    initScaData() {
        const emailValidators = [Validators.required, Validators.pattern(new RegExp(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/))];

    }

    getUserDetails() {
        this.userInfoService.getUser(this.userId).subscribe((item: User) => {
            this.user = item;
            this.updateUserForm.patchValue({
                email: this.user.email,
                pin: this.user.pin,
                login: this.user.login,
            });
            const scaUserData = <FormArray>this.updateUserForm.get('scaUserData');
            this.user.scaUserData.forEach((value, i) => {
                if (scaUserData.length < i + 1) {
                    scaUserData.push(this.initScaData());
                }
                scaUserData.at(i).patchValue(value);
            });
        });
    }

    addScaDataItem() {
        const control = <FormArray>this.updateUserForm.controls['scaUserData'];
        control.push(this.initScaData());
    }

    removeScaDataItem(i: number) {
        const control = <FormArray>this.updateUserForm.controls['scaUserData'];
        control.removeAt(i);
    }

    getMethodsValues() {
        this.methods = Object.keys(ScaMethods);
    }*/


