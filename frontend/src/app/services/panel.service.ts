import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {AuthenticationService} from "./authentication.service";
import {Observable} from "rxjs";
import {PageOfItems} from "../models/itemsPage.model";
import {environment as env} from "../../environments/environment";
import {GetItem, Item} from "../models/item.model";

@Injectable({
  providedIn: 'root'
})
export class PanelService {

  constructor(private http: HttpClient, private authenticationService: AuthenticationService) {
  }

  public getItems(todoListId: number, page: number, per_page: number, state: string): Observable<PageOfItems> {
    let params = new HttpParams();
    params = params.append('page', page);
    params = params.append('per_page', per_page);
    params = params.append('state', state);
    let userId = this.authenticationService.getTokenPayload("accessToken").sub;
    return this.http.get<PageOfItems>(
      `${env.backendUrl}/users/${userId}/lists/${todoListId}/items`,
      {params: params}
    )
  }

  public createItem(text: string, date: Date | null, todoListId: number): Observable<GetItem>{
    let userId = this.authenticationService.getTokenPayload("accessToken").sub;
    return this.http.post<GetItem>(
      `${env.backendUrl}/users/${userId}/lists/${todoListId}/items`,
      {text: text, expired: date == null? null : new Date(date).getTime()}
    )
  }

  public updateItem(item: Item): Observable<GetItem>{
    let userId = this.authenticationService.getTokenPayload("accessToken").sub;
    return this.http.put<GetItem>(
      `${env.backendUrl}/users/${userId}/lists/${item.todoListId}/items/${item.id}`,
      {text: item.text, state: item.state, expired: item.expired == null? null : new Date(item.expired).getTime()}
    )
  }

  public deleteItem(item: Item): Observable<void>{
    let userId = this.authenticationService.getTokenPayload("accessToken").sub;
    return this.http.delete<void>(
      `${env.backendUrl}/users/${userId}/lists/${item.todoListId}/items/${item.id}`
    )
  }
}
