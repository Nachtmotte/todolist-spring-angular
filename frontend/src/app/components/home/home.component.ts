import {Component} from '@angular/core';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {Observable} from 'rxjs';
import {map, shareReplay} from 'rxjs/operators';
import {NameDialogComponent} from "../name-dialog/name-dialog.component";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  constructor(private breakpointObserver: BreakpointObserver, public dialog: MatDialog) {
  }

  openDialog() {
    let dialogRef = this.dialog.open(NameDialogComponent, {
      data: {
        title: "Nueva carpeta",
        inputName: "nombre",
        inputData: "",
        buttonName: "crear"
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.state) {
        console.log(result.data)
      }
    });
  }

}
