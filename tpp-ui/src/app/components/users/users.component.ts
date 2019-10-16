import { Component, OnInit } from '@angular/core';

import { User } from '../../models/user.model';
import { UserService } from '../../services/user.service';
import {FilterPipe} from "ngx-filter-pipe";

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {
  users: User[];
  filteredUsers: User[];
  allUsers: User[];
  userFilter: any = {login: ''};
  config: {itemsPerPage, currentPage, totalItems, maxSize} = {
    itemsPerPage: 10,
    currentPage: 1,
    totalItems: 0,
    maxSize: 7
  };

  constructor(private userService: UserService, private filter: FilterPipe) {
    this.users = [];
    this.filteredUsers = [];
    this.allUsers = [];
  }

  ngOnInit() {
    this.listUsers(this.config.currentPage, this.config.itemsPerPage);
  }

  listUsers(page: number, size: number) {
    this.userService.listUsers(page - 1, size).subscribe(response => {
      this.users = response.users;
      this.config.totalItems = response.totalElements;

      this.getAllUsers();
    });
  }

  searchUsers(search: string) {
    console.log(search);
    let result = this.filter.transform(this.allUsers, this.userFilter);
    console.log(result);
    this.config.totalItems = result.length;
    if (this.config.totalItems < this.config.itemsPerPage) {
      this.users = result;
    } else {
      let startIndex = (this.config.currentPage - 1) * this.config.itemsPerPage;
      let endIndex = startIndex + this.config.itemsPerPage;
      this.users = result.slice(startIndex, endIndex);
    }
  }

  pageChange(pageNumber: number) {
    this.config.currentPage = pageNumber;

  }

  private getAllUsers() {
    this.userService.listUsers(0, this.config.totalItems).subscribe(response => {
      this.allUsers = response.users;
      console.log(this.allUsers);
    });
  }
}
