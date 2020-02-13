import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FundsConfirmationComponent } from './funds-confirmation.component';

describe('FundsConfirmationComponent', () => {
  let component: FundsConfirmationComponent;
  let fixture: ComponentFixture<FundsConfirmationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FundsConfirmationComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FundsConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
