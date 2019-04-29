import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountDetailComponent } from './account-detail.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientModule} from "@angular/common/http";
import {ReactiveFormsModule} from "@angular/forms";

describe('AccountDetailComponent', () => {
    let component: AccountDetailComponent;
    let fixture: ComponentFixture<AccountDetailComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                RouterTestingModule,
                HttpClientModule,
            ],
            declarations: [ AccountDetailComponent ]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AccountDetailComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
