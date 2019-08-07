import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ConsentsComponent} from './consents.component';
import {ReactiveFormsModule} from '@angular/forms';
import {RouterTestingModule} from '@angular/router/testing';
import {InfoModule} from '../common/info/info.module';

describe('ConsentsComponent', () => {
  let component: ConsentsComponent;
  let fixture: ComponentFixture<ConsentsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule, InfoModule],
      declarations: [ConsentsComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
