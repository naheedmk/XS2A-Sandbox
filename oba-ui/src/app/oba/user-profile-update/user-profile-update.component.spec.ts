import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { UserProfileUpdateComponent } from './user-profile-update.component';
import { ReactiveFormsModule } from '@angular/forms';
import {RouterTestingModule} from "@angular/router/testing";
import {OnlineBankingAccountInformationService} from "../../api/services";


describe('UserProfileEditComponent', () => {
  let component: UserProfileUpdateComponent;
  let fixture: ComponentFixture<UserProfileUpdateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserProfileUpdateComponent ],
      imports: [ ReactiveFormsModule, RouterTestingModule],
      providers: [ OnlineBankingAccountInformationService ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserProfileUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
