import {ProfilePicture, User} from "./user.model";

export class PageOfUsers {
  users: UsersPage;

  constructor(users: UsersPage) {
    this.users = users;
  }
}

export class UsersPage{
  first: boolean;
  last: boolean;
  number: number;
  numberOfElements: number;
  size: number;
  totalPages: number;
  totalElements: number;
  content: UserWithRoles[];

  constructor(first: boolean, last: boolean, number: number, numberOfElements: number, size: number, totalPages: number, totalElements: number, content: UserWithRoles[]) {
    this.first = first;
    this.last = last;
    this.number = number;
    this.numberOfElements = numberOfElements;
    this.size = size;
    this.totalPages = totalPages;
    this.totalElements = totalElements;
    this.content = content;
  }
}

export class UserWithRoles extends User {
  roles: Role[];

  constructor(id: number, firstname: string, lastname: string, email: string, username: string, created: Date, verified: boolean, profilePicture: ProfilePicture | null, roles: Role[]) {
    super(id, firstname, lastname, email, username, created, verified, profilePicture);
    this.roles = roles;
  }
}

export class Role {
  name: string;

  constructor(name: string) {
    this.name = name;
  }
}
