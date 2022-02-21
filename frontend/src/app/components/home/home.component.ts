import {Component, OnInit} from '@angular/core';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {Observable} from 'rxjs';
import {map, shareReplay} from 'rxjs/operators';
import {DialogComponent} from "../dialog/dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {MatSnackBar} from "@angular/material/snack-bar";
import {TodoList} from "../../models/todoList.model";
import {HomeService} from "../../services/home.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  folders: TodoList[] = [];
  folder: TodoList | null = null;
  loading: boolean = true;

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  constructor(private breakpointObserver: BreakpointObserver, private snackBar: MatSnackBar,
              public dialog: MatDialog, private homeService: HomeService) {
  }

  ngOnInit() {
    this.homeService.getTodoLists().subscribe(result => {
      this.folders = result.lists;
      if (this.folders.length == 0) {
        this.homeService.createTodoList("Tareas").subscribe(
          result => {
            this.folders.push(result.list);
            this.folder = this.folders[0]
            this.loading = false;
          }
        )
      } else {
        this.folder = this.folders[0]
        this.loading = false;
      }
    });
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
        if (this.folders.length >= 10) {
          this.openSnackBar("Solo se pueden crear hasta 10 carpetas")
        } else {
          this.homeService.createTodoList(result.data).subscribe(
            result => this.folders.push(result.list),
            () => this.openSnackBar("Hubo problemas creando la carpeta"));
        }
      }
    });
  }

  updateFolder() {
    let dialogRef = this.dialog.open(DialogComponent, {
      data: {
        isInput: true,
        isItem: false,
        title: "Editar carpeta",
        inputName: "nombre",
        inputData: this.folder?.name,
        buttonName: "actualizar"
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result?.state && this.folder) {
        this.homeService.editTodoList(this.folder.id, result.data).subscribe(
          // @ts-ignore
          result => this.folder.name = result.list.name,
          () => this.openSnackBar("Hubo problemas editando la carpeta"));
      }
    });
  }

  deleteFolder() {
    if (this.folder) {
      let dialogRef = this.dialog.open(DialogComponent, {
        data: {
          isInput: false,
          title: "Borrar carpeta",
          text: "¿Desea borrar la carpeta \"" + this.folder.name + "\" y todo su contenido?",
          buttonName: "borrar"
        }
      });
      dialogRef.afterClosed().subscribe(result => {
        if (result?.state) {
          if (this.folders.length > 1) {
            // @ts-ignore
            this.homeService.deleteTodoList(this.folder.id).subscribe(
              () => {
                this.folders = this.folders.filter(folder => folder !== this.folder);
                this.folder = this.folders[0];
              },
              () => this.openSnackBar("Hubo problemas borrando la carpeta"))
          } else {
            this.openSnackBar("No se puede borrar, debe haber almenos una carpeta");
          }
        }
      });
    }
  }

  openSnackBar(message: string) {
    this.snackBar.open(message, "Cerrar", {duration: 5000, panelClass: ['warning']});
  }
}
