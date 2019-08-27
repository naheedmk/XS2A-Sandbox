import {Component, OnInit} from '@angular/core';
import {UserService} from "../../../services/user.service";
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {User} from "../../../models/user.model";
import {Router} from "@angular/router";

@Component({
    selector: 'app-user-update',
    templateUrl: './user-update.component.html',
    styleUrls: ['./user-update.component.scss']
})
export class UserUpdateComponent implements OnInit {
    user: User;
    updateUserForm: FormGroup;
    public submitted: boolean;
    public errorMessage: string;


    constructor(private userService: UserService,
                private formBuilder: FormBuilder,
                private router: Router) {
    }

    ngOnInit() {
        this.setupUserFormControl();
    }

    setupUserFormControl(): void {
        this.updateUserForm = this.formBuilder.group({
            scaUserData: this.formBuilder.array([
                this.initScaData()
            ]),
            email: ['', [Validators.required, Validators.email]],
            login: ['', Validators.required],
            pin: ['', [Validators.required, Validators.minLength(5)]],
        });
    }

    get formControl() {
        return this.updateUserForm.controls;
    }

    onSubmit() {
        if (this.updateUserForm.invalid) {
            this.submitted = true;
            this.errorMessage = 'Please verify your credentials';
            return;
        }

        this.userService.updateUserDetails(this.updateUserForm.value)
            .subscribe(() => this.router.navigate(['/users/all'])
        )
    }

    initScaData() {
        return this.formBuilder.group({
            scaMethod: ['EMAIL', Validators.required],
            methodValue: ['', Validators.required],
            staticTan: [''],
            usesStaticTan: ['']
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


}
