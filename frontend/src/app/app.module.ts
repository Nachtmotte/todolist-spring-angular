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
import {NameDialogComponent} from './components/name-dialog/name-dialog.component';
import {FormsModule} from "@angular/forms";
import {MAT_DATE_LOCALE} from "@angular/material/core";


@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    HomeComponent,
    TodolistComponent,
    PanelComponent,
    NameDialogComponent
  ],
  entryComponents: [NameDialogComponent],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        AppRoutingModule,
        LayoutModule,
        HttpClientModule,
        MaterialModule,
        FormsModule
    ],
  providers: [
    {provide: MatPaginatorIntl, useValue: CustomPaginator()},
    { provide: MAT_DATE_LOCALE, useValue: 'es-ES' }
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
