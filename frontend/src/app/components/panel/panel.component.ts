import {Component, Input, OnInit, SimpleChanges} from '@angular/core';
import {ItemsPage} from "../../models/itemsPage.model";
import {Item} from "../../models/item.model";
import {DialogComponent} from "../dialog/dialog.component";
import {TodoList} from "../../models/todoList.model";
import {MatDialog} from "@angular/material/dialog";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-panel',
  templateUrl: './panel.component.html',
  styleUrls: ['./panel.component.css']
})
export class PanelComponent implements OnInit {

  @Input() title: string = '';
  @Input() expanded: boolean = false;
  @Input() state: string = '';
  @Input() newItem: Item | null = null;
  @Input() updatePanels: any | null = null;
  x: number = 0;

  ngOnChanges(changes: SimpleChanges) {
    if (changes['newItem'] && !changes['newItem'].firstChange) {
      //Consulta a la db para mandar item
      this.itemsPage.content.unshift(changes['newItem'].currentValue);
      this.itemsPage.totalElements += 1;
      if(this.itemsPage.numberOfElements == this.itemsPage.size){
        console.log(this.itemsPage.content.pop());
        this.itemsPage.totalPages = Math.ceil(this.itemsPage.totalElements/this.itemsPage.size);
        if(this.itemsPage.last){
          this.itemsPage.last = false;
        }
      }else{
        this.itemsPage.numberOfElements += 1;
      }
      console.log(changes['newItem'].currentValue);
      console.log(this.itemsPage);
    }
  }

  ngAfterViewChecked() {
    this.updatePanel();
  }

  itemsPage: ItemsPage = {
    first: true,
    last: true,
    number: 0,
    numberOfElements: 3,
    size: 10,
    totalPages: 1,
    totalElements: 3,
    content: [
      {
        id: 1,
        text: "Es un hecho establecido hace demasiado tiempo que un lector se distraerá con el contenido del texto de un sitio mientras que mira su diseño.",
        state: false,
        created: new Date(),
        expired: null,
        todoListId: 1,
      },
      {
        id: 2,
        text: "Es un hecho establecido hace demasiado tiempo que un lector se distraerá con el contenido del texto de un sitio mientras que mira su diseño.",
        state: false,
        created: new Date(),
        expired: null,
        todoListId: 1,
      },
      {
        id: 3,
        text: "Es un hecho establecido hace demasiado tiempo que un lector se distraerá con el contenido del texto de un sitio mientras que mira su diseño.",
        state: false,
        created: new Date(),
        expired: null,
        todoListId: 1,
      }
    ]
  }

  constructor(private snackBar: MatSnackBar, public dialog: MatDialog) {
  }

  ngOnInit(): void {
  }

  /*updateItem(item: Item){
    //actualizar item visualmente
    //mandar consulta para actualizar item
  }*/

  updateItem(item: Item) {
    let dialogRef = this.dialog.open(DialogComponent, {
      data: {
        isInput: true,
        isItem: true,
        title: "Editar tarea",
        inputName: "Tarea",
        inputData: item.text,
        inputDate: item.expired,
        buttonName: "actualizar"
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result?.state) {
        item.text = result.data.text;
        item.expired = result.data.date;
      } else {
        this.openSnackBar("No se edito");
      }
    });
  }

  deleteItem(item: Item){
    console.log('Eliminando item N°' + item.id);
    if(!this.itemsPage.last){
      console.log('Actualizando ' + this.state);
    }else if(!this.itemsPage.first && this.itemsPage.numberOfElements == 1){
      console.log('Actualizando pagina anterior ' + this.state);
    }else{
      this.itemsPage.content = this.itemsPage.content.filter(obj => obj !== item);
      this.itemsPage.totalElements -= 1;
      this.itemsPage.numberOfElements -= 1;
    }
  }

  changeStateItem(item: Item) {
    item.state = !item.state;
    this.sendUpdateAllPanels();
  }

  changeExpanded() {
    this.expanded = !this.expanded;
    this.updatePanel();
  }

  updatePanel() {
    if (this.expanded && this.updatePanels[this.state]) {
      console.log("Actualizando " + this.state);
      this.updatePanels[this.state] = false;
    }
  }

  sendUpdateAllPanels() {
    Object.keys(this.updatePanels).forEach(key => this.updatePanels[key] = true);
  }

  openSnackBar(message: string) {
    this.snackBar.open(message, "Cerrar", {duration: 60000, panelClass: ['warning']});
  }
}
