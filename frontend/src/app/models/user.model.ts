export class User {
  id: number;
  firstname: string;
  lastname: string;
  email: string;
  username: string;
  created: Date;
  verified: boolean;
  profilePicture: ProfilePicture | null;

  constructor(id: number, firstname: string, lastname: string, email: string, username: string, created: Date, verified: boolean, profilePicture: ProfilePicture | null) {
    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
    this.email = email;
    this.username = username;
    this.created = created;
    this.verified = verified;
    this.profilePicture = profilePicture;
  }
}

export class GetUser {
  user: User;

  constructor(user: User) {
    this.user = user;
  }
}

export class ProfilePicture {
  name: string;
  url: string;

  constructor(name: string, url: string) {
    this.name = name;
    this.url = url;
  }
}

export class UserPost {
  firstname: string;
  lastname: string;
  username: string;
  email: string;
  password: string;

  constructor(firstname: string, lastname: string, username: string, email: string, password: string) {
    this.firstname = firstname;
    this.lastname = lastname;
    this.username = username;
    this.email = email;
    this.password = password;
  }
}
