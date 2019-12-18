import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PeriodicPaymentsDetailsComponent } from './periodic-payments-details.component';

describe('PeriodicPaymentsDetailsComponent', () => {
  let component: PeriodicPaymentsDetailsComponent;
  let fixture: ComponentFixture<PeriodicPaymentsDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PeriodicPaymentsDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PeriodicPaymentsDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
