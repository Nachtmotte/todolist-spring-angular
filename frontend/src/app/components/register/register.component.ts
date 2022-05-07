import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, Validators, ValidatorFn } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { first } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import {UserService} from "../../services/user.service";
import {AuthenticationService} from "../../services/authentication.service";
import {UserPost} from "../../models/user.model";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  registerForm = this.formBuilder.group({
    firstName: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(45),
      Validators.pattern("^[a-zA-Z,.'-]+(\\s+[a-zA-Z,.'-]+)*$")
    ])],
    lastName: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(45),
      Validators.pattern("^[a-zA-Z,.'-]+(\\s+[a-zA-Z,.'-]+)*$")
    ])],
    username: ['', Validators.compose([
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(20),
      Validators.pattern("^(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$")
    ])],
    email: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(45),
      Validators.email
    ])],
    password: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(45),
      Validators.minLength(8)
    ])],
    repeatedPassword: ['', Validators.compose([
      Validators.required
    ])],
  });

  returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/home'
  loading = false;
  error = false;
  errorAction = "Cerrar";
  errorDurationInSeconds = 3;

  constructor(
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private authenticationService: AuthenticationService
  ) {
    this.registerForm.setValidators([
      this.comparePasswords(),
      this.validatePassword()
    ])
  }

  comparePasswords(): ValidatorFn {
    return (control: AbstractControl) => {
      const password = control.get('password');
      const repeatedPassword = control.get('repeatedPassword');

      if (password?.value !== repeatedPassword?.value) {
        repeatedPassword?.setErrors(
          { notequivalent: true }
        );
      }
      return null;
    };
  }

  validatePassword(): ValidatorFn {
    return (control: AbstractControl) => {
      const testLoweCase = new RegExp("^(?=.*[a-z])");
      const testUpperCase = new RegExp("^(?=.*[A-Z])");
      const testNumbers = new RegExp("^(?=.*[0-9])");
      const testSpaces = new RegExp("^(?!.*\\s)");

      const password = control.get('password');

      if (password?.value != '' && !testLoweCase.test(password?.value)) {
        password?.setErrors(
          { lowercase: true }
        );
      }
      else if (password?.value != '' && !testUpperCase.test(password?.value)) {
        password?.setErrors(
          { uppercase: true }
        );
      }
      else if (password?.value != '' && !testNumbers.test(password?.value)) {
        password?.setErrors(
          { number: true }
        );
      }
      else if (password?.value != '' && !testSpaces.test(password?.value)) {
        password?.setErrors(
          { space: true }
        );
      }
      return null;
    };
  }

  openSnackBar(errorMessage: string) {
    this.snackBar.open(
      errorMessage,
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

          this.openSnackBar("Ha ocurrido un error al Iniciar Session");
          this.router.navigate(["/login"]);
          this.loading = false;
        }
      });
  }

  executeRegister(requestUser: UserPost) {
    this.userService.createUser(requestUser)
      .pipe(first())
      .subscribe({
        next: () => {
          this.executeLogin(
            this.formControl['username'].value,
            this.formControl['password'].value
          )
        },
        error: (error) => {
          console.log(error)

          if (error.status == 409) {
            this.openSnackBar("Ya existe un Usuario con las mismas credenciales");
          }
          else {
            this.openSnackBar("Ha ocurrido un error Registrar");
          }
          this.loading = false;
        }
      });
  }

  get formControl() { return this.registerForm.controls; }

  onSubmit(): void {
    if (this.registerForm.invalid) return;

    this.loading = true;
    const user: UserPost = new UserPost(
      this.formControl['firstName'].value,
      this.formControl['lastName'].value,
      this.formControl['username'].value,
      this.formControl['email'].value,
      this.formControl['password'].value
    )

    this.executeRegister(user)
  }
}
