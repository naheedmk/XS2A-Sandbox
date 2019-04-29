import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountListComponent} from './account-list.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientModule} from "@angular/common/http";

describe('AccountListComponent', () => {
    let component: AccountListComponent;
    let fixture: ComponentFixture<AccountListComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                RouterTestingModule,
                HttpClientModule,
            ],
            declarations: [AccountListComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AccountListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
