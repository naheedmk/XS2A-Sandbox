import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {CustomizeService} from "../../common/services/customize.service";

@Component({
  selector: 'app-authorize',
  templateUrl: './authorize.component.html',
  styleUrls: ['./authorize.component.scss']
})
export class AuthorizeComponent implements OnInit {

  authorizeForm: FormGroup;

  constructor(
    public customizeService: CustomizeService,
    private formBuilder: FormBuilder,
    private router: Router
  ) { }

  ngOnInit() {
    this.initAuthorizeForm();
  }

  private initAuthorizeForm(): void {
    this.authorizeForm = this.formBuilder.group({
      login: ['', Validators.required],
      pin: ['', Validators.required],
      redirectUri: ['', Validators.required]
    });
  }

}
