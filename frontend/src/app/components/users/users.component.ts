import {Component, OnInit, ViewChild} from '@angular/core';
import {UsersPage, UserWithRoles} from "../../models/usersPage.model";
import {AuthenticationService} from "../../services/authentication.service";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {

  usersPage: UsersPage = {
    first: true,
    last: true,
    number: 0,
    numberOfElements: 0,
    size: 5,
    totalPages: 0,
    totalElements: 0,
    content: []
  }
  displayedColumns: string[] = ['firstname', 'lastname', 'email', 'username', 'created', 'roles'];
  dataSource: UserWithRoles[] = this.usersPage.content;

  constructor(private authenticationService: AuthenticationService, private userService: UserService) {
  }

  ngOnInit(): void {
    if (this.authenticationService.getTokenPayload("accessToken").roles.includes("ROLE_ADMIN")) {
      this.getUsers(this.usersPage.number, this.usersPage.size);
    }
  }

  getUsers(page: number, per_page: number){
    this.userService.getUsers(page, per_page)
      .subscribe(result => {
        this.usersPage = result.users;
        this.dataSource = this.usersPage.content;
      });
  }
}

