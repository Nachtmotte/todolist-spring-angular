import {NgModule} from '@angular/core';
import {BrowserModule, DomSanitizer} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AppRoutingModule} from './app-routing.module';
import {NavbarComponent} from './components/navbar/navbar.component';
import {HomeComponent} from './components/home/home.component';
import {LayoutModule} from '@angular/cdk/layout';
import {MaterialModule} from "./material/material.module";
import {MatIconRegistry} from "@angular/material/icon";
import {HttpClientModule} from "@angular/common/http";
import {TodolistComponent} from './components/todolist/todolist.component';
import {PanelComponent} from './components/panel/panel.component';
import {MatPaginatorIntl} from '@angular/material/paginator';
import {CustomPaginator} from "./material/CustomPaginatorConfiguration";

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    HomeComponent,
    TodolistComponent,
    PanelComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    LayoutModule,
    HttpClientModule,
    MaterialModule
  ],
  providers: [
    {provide: MatPaginatorIntl, useValue: CustomPaginator()}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {

  constructor(
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer
  ) {
    this.matIconRegistry.addSvgIcon(
      `check-logo`,
      this.domSanitizer.bypassSecurityTrustResourceUrl("../assets/check-logo.svg"));
  }
}
