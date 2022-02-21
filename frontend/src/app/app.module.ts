import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AppRoutingModule} from './app-routing.module';
import {NavbarComponent} from './components/navbar/navbar.component';
import {HomeComponent} from './components/home/home.component';
import {LayoutModule} from '@angular/cdk/layout';
import {MaterialModule} from "./components/material/material.module";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {TodolistComponent} from './components/todolist/todolist.component';
import {PanelComponent} from './components/panel/panel.component';
import {MatPaginatorIntl} from '@angular/material/paginator';
import {DialogComponent} from './components/dialog/dialog.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MAT_DATE_LOCALE} from "@angular/material/core";
import {getSpanishPaginatorIntl} from "./components/material/spanish-paginator-intl";
import {LoginComponent} from './components/login/login.component';
import {JwtInterceptor} from "./interceptors/jwt.interceptor";
import {ProfileComponent} from './components/profile/profile.component';
import {RegisterComponent} from './components/register/register.component';
import { UsersComponent } from './components/users/users.component';
import {MatTableModule} from "@angular/material/table";


@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    HomeComponent,
    TodolistComponent,
    PanelComponent,
    DialogComponent,
    LoginComponent,
    ProfileComponent,
    RegisterComponent,
    UsersComponent
  ],
  entryComponents: [DialogComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    LayoutModule,
    HttpClientModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    MatTableModule,
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true},
    {provide: MatPaginatorIntl, useValue: getSpanishPaginatorIntl()},
    {provide: MAT_DATE_LOCALE, useValue: 'es-ES'},
  ],
  bootstrap: [AppComponent]
})
export class AppModule {

  constructor() {
  }
}
