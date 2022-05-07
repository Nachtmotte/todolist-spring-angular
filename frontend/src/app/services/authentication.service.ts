import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment as env} from 'src/environments/environment';
import {JwtHelperService} from '@auth0/angular-jwt';

@Injectable({providedIn: 'root'})
export class AuthenticationService {

  jwtHelper = new JwtHelperService();

  constructor(
    private http: HttpClient,
  ) {
  }

  getAccessTokenPayload() {
    const tokenValidation = this.isTokenValid("accessToken");
    const tokenExpiration = this.isTokenExpired("accessToken");
    const tokenPayload = this.getTokenPayload("accessToken");
    return tokenValidation && !tokenExpiration ? tokenPayload : null
  }

  getTokenPayload(token: string) {
    const rawToken = sessionStorage.getItem(token);
    let payloadToken = null
    try {
      payloadToken = rawToken == null ? null : this.jwtHelper.decodeToken(rawToken);
    } catch (error) {
      console.log(error)
    }

    return payloadToken
  }

  isTokenValid(token: string) {
    return this.getTokenPayload(token) != null;
  }

  isTokenExpired(token: string) {
    const rawToken = sessionStorage.getItem(token);
    let isExpired = false
    try {
      isExpired = rawToken == null || this.jwtHelper.isTokenExpired(rawToken);
    } catch (error) {
      console.log(error)
    }

    return isExpired
  }

  login(username: string, password: string) {
    const formData = new FormData();

    formData.append('username', username);
    formData.append('password', password);

    return this.http.post<any>(`${env.backendUrl}/login`, formData)
  }

  logout() {
    sessionStorage.removeItem('accessToken');
    sessionStorage.removeItem('refreshToken');
  }

  refreshToken() {
    const rawToken = sessionStorage.getItem('refreshToken');
    return this.http.get<any>(`${env.backendUrl}/token/refresh`,
      {
        headers: {'Authorization': `Bearer ${rawToken}`}
      })
  }
}
