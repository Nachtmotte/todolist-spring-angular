import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {DialogComponent} from "../dialog/dialog.component";
import {TodoList} from "../../models/todoList.model";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatDialog} from "@angular/material/dialog";
import {Item} from "../../models/item.model";

@Component({
  selector: 'app-todolist',
  templateUrl: './todolist.component.html',
  styleUrls: ['./todolist.component.css']
})
export class TodolistComponent implements OnInit {

  @Input() folder: TodoList = {id: 0, name: "", created: new Date()}
  @Output() updateFolderEvent: EventEmitter<void>;
  @Output() deleteFolderEvent: EventEmitter<void>;
  @ViewChild('menuTrigger') calendar: any;
  minDate: Date = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);
  selectedDate: Date | null = null;
  text: string = "";
  newItem: Item | null = null;
  updatePanels: any = {unchecked: true, checked: true, expired: true};

  constructor(private snackBar: MatSnackBar, public dialog: MatDialog) {
    this.updateFolderEvent = new EventEmitter<void>();
    this.deleteFolderEvent = new EventEmitter<void>();
  }

  ngOnInit(): void {
  }

  saveDate(event: Date) {
    this.selectedDate = event;
    this.calendar.closeMenu();
  }

  saveItem() {
    this.newItem = new Item(null, this.text, false, null, this.selectedDate, this.folder.id);
    this.selectedDate = null;
    this.text = "";
  }

  sendFolderUpdateEvent(): void {
    this.updateFolderEvent.emit();
  }

  sendFolderDeleteEvent(): void {
    this.deleteFolderEvent.emit();
  }

  updateFolder() {
    let dialogRef = this.dialog.open(DialogComponent, {
      data: {
        isInput: true,
        isItem: false,
        title: "Editar carpeta",
        inputName: "nombre",
        inputData: this.folder.name,
        buttonName: "actualizar"
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result?.state) {
        this.folder.name = result.data;
        this.sendFolderUpdateEvent();
      } else {
        this.openSnackBar("No se edito");
      }
    });
  }

  deleteFolder() {
    let dialogRef = this.dialog.open(DialogComponent, {
      data: {
        isInput: false,
        title: "Borrar carpeta",
        text: "¿Desea borrar la carpeta" + this.folder.name + "y todo su contenido?",
        buttonName: "borrar"
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result?.state) {
        this.sendFolderDeleteEvent();
      } else {
        this.openSnackBar("No se borro");
      }
    });
  }

  openSnackBar(message: string) {
    this.snackBar.open(message, "Cerrar", {duration: 60000, panelClass: ['warning']});
  }
}
