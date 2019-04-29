import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CashDepositComponent} from './cash-deposit.component';
import {ReactiveFormsModule} from "@angular/forms";
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientModule} from "@angular/common/http";

describe('CashDepositComponent', () => {
    let component: CashDepositComponent;
    let fixture: ComponentFixture<CashDepositComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                RouterTestingModule,
                HttpClientModule,
            ],
            declarations: [CashDepositComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(CashDepositComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
