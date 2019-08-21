import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import { OnlineBankingAuthorizationService} from "../api/services/online-banking-authorization.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-reset-password',
    templateUrl: './reset-password.component.html',
    styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {
    resetPasswordForm: FormGroup;
    public submitted: boolean;
    public errorMessage: string;

    constructor(
        private authService: OnlineBankingAuthorizationService,
        private formBuilder: FormBuilder,
        private router: Router) {
    }

    ngOnInit() {
        this.resetPasswordForm = this.formBuilder.group({
            email: ['', [Validators.required, Validators.pattern(new RegExp(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/)),]],
            login: ['', Validators.required],
        });
    }

    onSubmit() {
        if (this.resetPasswordForm.invalid) {
            this.submitted = true;
            this.errorMessage = 'Please enter your credentials';
            return;
        }

      this.authService.sendCodeUsingPOST(this.resetPasswordForm.value)
            .subscribe(() => this.router.navigate(['/confirm-password']));
    }
}
