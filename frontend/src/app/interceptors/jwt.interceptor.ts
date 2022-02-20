import {Injectable} from '@angular/core';
import {HttpRequest, HttpHandler, HttpEvent, HttpInterceptor} from '@angular/common/http';
import {switchMap, catchError} from 'rxjs/operators';
import {Observable, EMPTY} from 'rxjs';
import {Router} from '@angular/router';
import {AuthenticationService} from "../services/authentication.service";
import {environment as env} from "../../environments/environment";

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(
    private authenticationService: AuthenticationService,
    private router: Router
  ) {
  }

  redirectToLogin() {
    this.router.navigate(['/login']);
  }

  setRefreshToken(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return this.authenticationService.refreshToken()
      .pipe(
        switchMap(bodyResponse => {
          sessionStorage.setItem('accessToken', bodyResponse.accessToken);
          sessionStorage.setItem('refreshToken', bodyResponse.refreshToken);

          return this.setAccessTokenToRequest(request, next);
        }),
        catchError(error => {
          console.log(error);

          this.authenticationService.logout();
          this.redirectToLogin();

          return EMPTY;
        })
      );
  }

  setAccessTokenToRequest(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const rawToken = sessionStorage.getItem("accessToken");

    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${rawToken}`
      }
    });
    return next.handle(request);
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if ((request.url == `${env.backendUrl}/token/refresh` && request.method == 'GET') ||
      (request.url == `${env.backendUrl}/login` || request.url == `${env.backendUrl}/users`
        && request.method == 'POST')) {
      return next.handle(request);
    }

    const accessTokenValidation = this.authenticationService.isTokenValid("accessToken");
    if (!accessTokenValidation) {
      this.authenticationService.logout();
      this.redirectToLogin();
      return EMPTY;
    }

    const accessTokenExpiration = this.authenticationService.isTokenExpired("accessToken");
    if (accessTokenExpiration) {
      const refreshTokenExpiration = this.authenticationService.isTokenExpired("refreshToken");
      const refreshTokenValidation = this.authenticationService.isTokenValid("refreshToken");
      if (!refreshTokenValidation || refreshTokenExpiration) {
        this.authenticationService.logout();
        this.redirectToLogin();
        return EMPTY;
      } else {
        return this.setRefreshToken(request, next);
      }
    } else {
      return this.setAccessTokenToRequest(request, next);
    }
  }
}
