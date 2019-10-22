import { Component, OnInit } from '@angular/core';

import { User } from '../../models/user.model';
import { UserService } from '../../services/user.service';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {
  users: User[];
  searchForm: FormGroup;
  config: {itemsPerPage, currentPage, totalItems, maxSize} = {
    itemsPerPage: 10,
    currentPage: 1,
    totalItems: 0,
    maxSize: 7
  };

  constructor(
    private userService: UserService,
    private formBuilder: FormBuilder) {
    this.users = [];
  }

  ngOnInit() {
    this.searchForm = this.formBuilder.group({
      query: ['', Validators.required]
    });
    this.listUsers(this.config.currentPage, this.config.itemsPerPage);

    this.onQueryUsers();
  }

  listUsers(page: number, size: number, queryParam: string = '') {
    this.userService.listUsers(page - 1, size, queryParam).subscribe(response => {
      this.users = response.users;
      this.config.totalItems = response.totalElements;
    });
  }

  pageChange(pageNumber: number) {
    this.config.currentPage = pageNumber;
    this.listUsers(pageNumber, this.config.itemsPerPage);
  }

  onQueryUsers() {
    this.searchForm.valueChanges.subscribe(form => {
      this.listUsers(this.config.currentPage, this.config.itemsPerPage, form.query);
    });
  }
}
