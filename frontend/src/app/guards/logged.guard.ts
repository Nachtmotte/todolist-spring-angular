import {Injectable} from '@angular/core';
import {Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';
import {AuthenticationService} from '../services/authentication.service';

@Injectable({providedIn: 'root'})
export class LoggedGuard implements CanActivate {
  constructor(
    private router: Router,
    private authenticationService: AuthenticationService
  ) {
  }

  redirectToHome(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    this.router.navigate(['/home']);
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const accessTokenValidation = this.authenticationService.isTokenValid("accessToken");
    const accessTokenExpiration = this.authenticationService.isTokenExpired("accessToken");
    const refreshTokenValidation = this.authenticationService.isTokenValid("refreshToken");
    const refreshTokenExpiration = this.authenticationService.isTokenExpired("refreshToken");
    if ((accessTokenValidation && !accessTokenExpiration) || (accessTokenExpiration && refreshTokenValidation && !refreshTokenExpiration)) {
      this.redirectToHome(route, state);
      return false;
    } else {
      this.authenticationService.logout();
      return true;
    }
  }
}
