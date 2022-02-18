import {Component} from '@angular/core';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {Observable} from 'rxjs';
import {map, shareReplay} from 'rxjs/operators';
import {DialogComponent} from "../dialog/dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {MatSnackBar} from "@angular/material/snack-bar";
import {TodoList} from "../../models/todoList.model";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {

  folders: TodoList[] = [
    {
      id: 1,
      name: "carpeta1",
      created: new Date(),
    },
    {
      id: 2,
      name: "carpeta2",
      created: new Date(),
    },
    {
      id: 3,
      name: "carpeta3",
      created: new Date(),
    }
  ];
  folder: TodoList = this.folders[0];

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  constructor(private breakpointObserver: BreakpointObserver, private snackBar: MatSnackBar, public dialog: MatDialog) {
  }

  changeFolder(folder: TodoList) {
    this.folder = folder;
  }

  createFolder() {
    let dialogRef = this.dialog.open(DialogComponent, {
      data: {
        isInput: true,
        title: "Nueva carpeta",
        inputName: "nombre",
        inputData: "",
        buttonName: "crear"
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result?.state) {
        let newFolder: TodoList = {
          id: this.folders.length + 1,
          name: result.data,
          created: new Date()
        }
        this.folders.push(newFolder);
      } else {
        this.openSnackBar("No se creo");
      }
    });
  }

  updateFolder() {
    console.log(this.folders);
  }

  deleteFolder() {
    if (this.folders.length > 1) {
      this.folders = this.folders.filter(folder => folder !== this.folder);
      this.folder = this.folders[0];
    } else {
      this.openSnackBar("No se puede borrar, debe haber almenos una carpeta");
    }
  }

  openSnackBar(message: string) {
    this.snackBar.open(message, "Cerrar", {duration: 60000, panelClass: ['warning']});
  }
}
