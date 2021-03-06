import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "../../services/authentication.service";
import {NavigationStart, Router} from "@angular/router";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  isLogged: boolean = false;
  isAdmin: boolean = false;
  username: string | null = null;

  constructor(private authenticationService: AuthenticationService, private router: Router, private userService: UserService) {

  }

  ngOnInit(): void {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        this.isAdmin = this.authenticationService.getTokenPayload("accessToken").roles.includes("ROLE_ADMIN");
        this.isLogged = this.authenticationService.getAccessTokenPayload() != null;
        if (this.isLogged && this.username == null) {
          this.userService.getUser().subscribe(
            result => this.username = result.user.username
          );
        }
      }
    });
  }

  logout() {
    this.authenticationService.logout();
    this.username = null;
    this.isLogged = false;
    this.isAdmin = false;
    this.router.navigate(['/login']);
  }
}
