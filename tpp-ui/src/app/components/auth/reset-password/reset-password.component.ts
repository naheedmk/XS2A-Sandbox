import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../../services/auth.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-reset-password',
    templateUrl: './reset-password.component.html',
    styleUrls: ['../auth.component.scss']
})
export class ResetPasswordComponent implements OnInit {
    switchTemplate: boolean;
    resetPasswordForm: FormGroup;
    confirmNewPasswordForm: FormGroup;
    public submitted: boolean;
    public errorMessage: string;

    constructor(
        private authService: AuthService,
        private formBuilder: FormBuilder,
        private router: Router) {
    }

    ngOnInit() {
        this.resetPasswordForm = this.formBuilder.group({
            email: ['', [Validators.required, Validators.pattern(new RegExp(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/)),]],
            login: ['', Validators.required],
        });

        this.confirmNewPasswordForm = this.formBuilder.group({
            newPassword: ['', Validators.required],
            code: [''],
        });
    }

    onSubmit() {
        if (this.resetPasswordForm.invalid) {
            this.submitted = true;
            this.errorMessage = 'Please enter your credentials';
            return;
        }

        this.authService.requestCodeForResetPassword(this.resetPasswordForm.value)
            .subscribe(success => {
                if (success) {
                    console.log(success);
                    this.switchTemplate = true;
                    this.confirmNewPasswordForm.get('code').setValue(success.code);
                    //this.router.navigate(['/']);
                } else {
                    this.errorMessage = 'Invalid credentials'
                }
            });
    }

    onPasswordSubmit() {
        if (this.confirmNewPasswordForm.invalid) {
            this.submitted = true;
            return;
        }

        this.authService.changePassword(this.confirmNewPasswordForm.value)
            .subscribe(success => {
                if (success) {
                    console.log(success);
                    this.router.navigate(['/login']);
                }
            });
    }
}
