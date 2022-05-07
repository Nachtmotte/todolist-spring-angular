import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {GetTodoLists, GetTodoList} from "../models/todoList.model";
import {environment as env} from 'src/environments/environment';
import {AuthenticationService} from "./authentication.service";

@Injectable({
  providedIn: 'root'
})
export class HomeService {

  constructor(private http: HttpClient, private authenticationService: AuthenticationService) {
  }

  public getTodoLists(): Observable<GetTodoLists> {
    return this.http.get<GetTodoLists>(
      `${env.backendUrl}/users/${this.authenticationService.getTokenPayload("accessToken").sub}/lists`)
  }

  public createTodoList(name: string): Observable<GetTodoList> {
    return this.http.post<GetTodoList>(
      `${env.backendUrl}/users/${this.authenticationService.getTokenPayload("accessToken").sub}/lists`,
      {name: name}
    )
  }

  public editTodoList(id: number, name: string): Observable<GetTodoList> {
    return this.http.put<GetTodoList>(
      `${env.backendUrl}/users/${this.authenticationService.getTokenPayload("accessToken").sub}/lists/${id}`,
      {name: name}
    )
  }

  public deleteTodoList(id: number): Observable<any> {
    return this.http.delete<any>(
      `${env.backendUrl}/users/${this.authenticationService.getTokenPayload("accessToken").sub}/lists/${id}`
    )
  }
}
