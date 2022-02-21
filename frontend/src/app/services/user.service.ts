import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AuthenticationService} from "./authentication.service";
import {Observable} from "rxjs";
import {GetUser, UserPost} from "../models/user.model";
import {environment as env} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient, private authenticationService: AuthenticationService) {
  }

  public getUser(): Observable<GetUser> {
    return this.http.get<GetUser>(`${env.backendUrl}/users/${this.authenticationService.getTokenPayload("accessToken").sub}`)
  }

  public createUser(user: UserPost): Observable<GetUser>{
    return this.http.post<GetUser>(
      `${env.backendUrl}/users`,
      {
        firstname: user.firstname,
        lastname: user.lastname,
        email: user.email,
        username: user.username,
        password: user.password
      })
  }
}
