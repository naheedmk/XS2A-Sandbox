import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PeriodicPaymentsComponent } from './periodic-payments.component';

describe('PeriodicPaymentsComponent', () => {
    let component: PeriodicPaymentsComponent;
    let fixture: ComponentFixture<PeriodicPaymentsComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [ PeriodicPaymentsComponent ]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(PeriodicPaymentsComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
