import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {ReactiveFormsModule} from "@angular/forms";
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientModule} from "@angular/common/http";
import {UserCreateComponent} from './user-create.component';
import {IconModule} from "../../../commons/icon/icon.module";

describe('UserCreateComponent', () => {
    let component: UserCreateComponent;
    let fixture: ComponentFixture<UserCreateComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                RouterTestingModule,
                HttpClientModule,
                IconModule
            ],
            declarations: [UserCreateComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(UserCreateComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
