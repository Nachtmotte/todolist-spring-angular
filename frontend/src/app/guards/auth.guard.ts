import {Injectable} from '@angular/core';
import {Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';
import {map, catchError} from 'rxjs/operators';
import {of} from 'rxjs';
import {AuthenticationService} from '../services/authentication.service';

@Injectable({providedIn: 'root'})
export class AuthGuard implements CanActivate {
  constructor(
    private router: Router,
    private authenticationService: AuthenticationService
  ) {
  }

  redirectToLogin(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    this.router.navigate(['/login'], {queryParams: {returnUrl: state.url}});
  }

  setRefreshToken(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.authenticationService.refreshToken()
      .pipe(
        map(bodyResponse => {
          sessionStorage.setItem('accessToken', bodyResponse.accessToken);
          sessionStorage.setItem('refreshToken', bodyResponse.refreshToken);

          return true;
        }),
        catchError(error => {
          console.log(error);

          this.authenticationService.logout();
          this.redirectToLogin(route, state);

          return of(false);
        })
      );
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const accessTokenValidation = this.authenticationService.isTokenValid("accessToken");
    if (!accessTokenValidation) {
      this.authenticationService.logout();
      this.redirectToLogin(route, state);
      return false;
    }

    const accessTokenExpiration = this.authenticationService.isTokenExpired("accessToken");
    if (accessTokenExpiration) {
      const refreshTokenExpiration = this.authenticationService.isTokenExpired("refreshToken");
      const refreshTokenValidation = this.authenticationService.isTokenValid("refreshToken");
      if (!refreshTokenValidation || refreshTokenExpiration) {
        this.authenticationService.logout();
        this.redirectToLogin(route, state);
        return false;
      } else {
        return this.setRefreshToken(route, state);
      }
    } else {
      return true;
    }
  }
}
