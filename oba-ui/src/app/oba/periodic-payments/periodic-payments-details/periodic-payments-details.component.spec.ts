import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PeriodicPaymentsDetailsComponent } from './periodic-payments-details.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('PeriodicPaymentsDetailsComponent', () => {
  let component: PeriodicPaymentsDetailsComponent;
  let fixture: ComponentFixture<PeriodicPaymentsDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PeriodicPaymentsDetailsComponent ],
      imports: [RouterTestingModule, HttpClientTestingModule],
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
