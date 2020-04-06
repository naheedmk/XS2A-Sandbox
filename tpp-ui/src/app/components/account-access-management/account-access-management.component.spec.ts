import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import { DebugElement } from '@angular/core';
import {User} from "../../models/user.model";
import {AccountAccessManagementComponent} from './account-access-management.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {AccountService} from "../../services/account.service";
import {UserService} from "../../services/user.service";
import {RouterTestingModule} from "@angular/router/testing";
import {InfoModule} from "../../commons/info/info.module";
import {InfoService} from "../../commons/info/info.service";
import {NgbTypeaheadModule} from "@ng-bootstrap/ng-bootstrap";
import {Router, ActivatedRoute} from "@angular/router";
import { AccountStatus, AccountType, UsageType } from '../../models/account.model';
import {Account} from "../../models/account.model";
import { of } from 'rxjs';
import get = Reflect.get;

describe('AccountAccessManagementComponent', () => {
    let component: AccountAccessManagementComponent;
    let fixture: ComponentFixture<AccountAccessManagementComponent>;
    let accountService: AccountService;
    let userService: UserService;
    let infoService: InfoService;
    let router: Router;
    let route: ActivatedRoute;
    let de: DebugElement;
    let el: HTMLElement;
    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                HttpClientTestingModule,
                RouterTestingModule,
                NgbTypeaheadModule,
                InfoModule,
                FormsModule,
            ],
            declarations: [AccountAccessManagementComponent],
            providers: [AccountService, InfoService]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AccountAccessManagementComponent);
        component = fixture.componentInstance;
        accountService = TestBed.get(AccountService);
        userService = TestBed.get(UserService);
        infoService = TestBed.get(InfoService);
        router = TestBed.get(Router);
        route = TestBed.get(ActivatedRoute)
        fixture.detectChanges();
        component.ngOnInit();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('submitted should false', () => {
        expect(component.submitted).toBeFalsy();
    });

    it('should load the ngoninit',() =>  {
        component.ngOnInit();
    });

    it('accountAccessForm should be invalid when at least one field is empty', () => {
        expect(component.accountAccessForm.valid).toBeFalsy();
    });

    it('validate onSubmit method', () => {
        component.onSubmit();
        expect(component.submitted).toEqual(true);
        expect(component.accountAccessForm.valid).toBeFalsy();
        console.log('submitted', component.submitted);
    });

    it('validate setupAccountAccessFormControl method', () => {
        component.setupAccountAccessFormControl();
        expect(component.accountAccessForm).toBeDefined();
    });

    it('should call the inputFormatterValue', () => {
        let mockUser: User =
            {
                id: 'USERID',
                email: 'user@gmail.com',
                login: 'user',
                branch: 'branch',
                pin: '12345',
                scaUserData: [],
                accountAccesses: []
            }
        component.inputFormatterValue(mockUser);
        expect(mockUser.login).toEqual('user');
    });

    it('should return a inputFormatterValue ', () => {
        let mockUser: User =
            {
                id: 'USERID',
                email: 'user@gmail.com',
                login: '',
                branch: 'branch',
                pin: '12345',
                scaUserData: [],
                accountAccesses: []
            }
        component.inputFormatterValue(null);
        expect(mockUser);
    });

    it('should call the resultFormatterValue', () => {
        let mockUser: User =
            {
                id: 'USERID',
                email: 'user@gmail.com',
                login: 'user',
                branch: 'branch',
                pin: '12345',
                scaUserData: [],
                accountAccesses: []
            }
        component.resultFormatterValue(mockUser);
        expect(mockUser.login).toEqual('user');
    });

    it('should return a resultFormatterValue ', () => {
        let mockUser: User =
            {
                id: 'USERID',
                email: 'user@gmail.com',
                login: '',
                branch: 'branch',
                pin: '12345',
                scaUserData: [],
                accountAccesses: []
            }
        component.resultFormatterValue(null);
        expect(mockUser);
    });

    it('should load listUsers', () => {
        const mockUsers: User[] = [
            {
                id: 'USERID',
                email: 'user@gmail.com',
                login: 'user',
                branch: 'branch',
                pin: '12345',
                scaUserData: [],
                accountAccesses: []
            }
        ];

        let listUsersSpy = spyOn(userService, 'listUsers').and.returnValue(of({users: mockUsers}));;
        component.listUsers();
        expect(component.users).toEqual(mockUsers);
    });
});
