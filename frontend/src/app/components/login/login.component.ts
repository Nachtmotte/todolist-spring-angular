import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import { Router, ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import {AuthenticationService} from "../../services/authentication.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  loginForm = this.formBuilder.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/home'
  loading = false;
  error = false;
  errorMessage = "Nombre de usuario/Correo y/o Contraseña Incorrecta";
  errorAction = "Cerrar";
  errorDurationInSeconds = 3;

  constructor(
    private formBuilder: FormBuilder,
    private authenticationService: AuthenticationService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) { }

  openSnackBar() {
    this.snackBar.open(
      this.errorMessage,
      this.errorAction,
      {
        duration: this.errorDurationInSeconds * 1000,
        panelClass: ['warning']
      });
  }

  executeLogin(username: string, password: string) {
    this.authenticationService.login(username, password)
      .pipe(first())
      .subscribe({
        next: (bodyResponse) => {
          sessionStorage.setItem('accessToken', bodyResponse.accessToken);
          sessionStorage.setItem('refreshToken', bodyResponse.refreshToken);

          this.router.navigate([this.returnUrl]);
        },
        error: (error) => {
          console.log(error)

          this.openSnackBar();
          this.loading = false;
        }
      });
  }

  get formControl() { return this.loginForm.controls; }

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    this.loading = true;

    this.executeLogin(
      this.formControl['username'].value,
      this.formControl['password'].value
    )
  }
}
