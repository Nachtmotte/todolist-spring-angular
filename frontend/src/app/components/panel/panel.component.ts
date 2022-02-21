import {Component, Input, SimpleChanges} from '@angular/core';
import {ItemsPage} from "../../models/itemsPage.model";
import {Item} from "../../models/item.model";
import {DialogComponent} from "../dialog/dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {MatSnackBar} from "@angular/material/snack-bar";
import {TodoList} from "../../models/todoList.model";
import {PanelService} from "../../services/panel.service";

@Component({
  selector: 'app-panel',
  templateUrl: './panel.component.html',
  styleUrls: ['./panel.component.css']
})
export class PanelComponent {

  @Input() title: string = '';
  @Input() expanded: boolean = false;
  @Input() state: string = '';
  @Input() todoList: TodoList | null = null;
  @Input() newItem: Item | null = null;
  @Input() updatePanels: any | null = null;
  loading: boolean = true;
  itemsPage: ItemsPage = {
    first: true,
    last: true,
    number: 0,
    numberOfElements: 0,
    size: 5,
    totalPages: 0,
    totalElements: 0,
    content: []
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['newItem'] && !changes['newItem'].firstChange && this.todoList) {
      let newItemCurrentValue: Item = changes['newItem'].currentValue;
      this.panelService.createItem(newItemCurrentValue.text, newItemCurrentValue.expired, this.todoList.id)
        .subscribe(result => {
          this.itemsPage.content.unshift(result.item);
          this.itemsPage.totalElements += 1;
          if (this.itemsPage.numberOfElements == this.itemsPage.size) {
            this.itemsPage.content.pop();
            this.itemsPage.totalPages = Math.ceil(this.itemsPage.totalElements / this.itemsPage.size);
            if (this.itemsPage.last) {
              this.itemsPage.last = false;
            }
          } else {
            this.itemsPage.numberOfElements += 1;
          }
        }, () => this.openSnackBar("Hubo problemas al crear la tarea"));
    }
    if (changes['todoList'] && !changes['todoList'].firstChange && this.todoList) {
      this.updatePanels[this.state] = true;
      this.updatePanel();
    }
  }

  ngAfterViewChecked() {
    this.updatePanel();
  }

  constructor(private snackBar: MatSnackBar, public dialog: MatDialog, private panelService: PanelService) {
  }

  getItems(page: number, per_page: number) {
    if (this.todoList) {
      //Promise prevent error NG0100
      Promise.resolve().then(() => this.loading = true);
      this.panelService.getItems(this.todoList.id, page, per_page, this.state).subscribe(
        result => {
          this.itemsPage = result.items;
          this.loading = false;
        },
        () => this.openSnackBar("Hubo problemas al cargar las tareas")
      );
      this.updatePanels[this.state] = false;
    }
  }

  changeExpanded() {
    this.expanded = !this.expanded;
    this.updatePanel();
  }

  updatePanel() {
    if (this.expanded && this.updatePanels[this.state]) {
      this.getItems(this.itemsPage.number, this.itemsPage.size);
    }
  }

  changeStateItem(item: Item) {
    item.state = !item.state;
    this.panelService.updateItem(item).subscribe(
      () => {
        if (!this.itemsPage.last) {
          Object.keys(this.updatePanels).forEach(key => this.updatePanels[key] = true);
        } else if (!this.itemsPage.first && this.itemsPage.numberOfElements == 1) {
          this.itemsPage.number -= 1;
          Object.keys(this.updatePanels).forEach(key => this.updatePanels[key] = true);
        } else {
          Object.keys(this.updatePanels).forEach(key => {
            if (key != this.state) {
              this.updatePanels[key] = true
            }
          });
          this.itemsPage.content = this.itemsPage.content.filter(obj => obj !== item);
          this.itemsPage.totalElements -= 1;
          this.itemsPage.numberOfElements -= 1;
        }
      },
      () => {
        this.openSnackBar("Hubo problemas al editar la tarea")
        item.state = !item.state;
      }
    )
  }

  updateItemTextAndExpired(item: Item) {
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
        let oldText = item.text;
        let oldExpired = item.expired;
        item.text = result.data.text;
        item.expired = result.data.date;
        this.panelService.updateItem(item).subscribe(() => {
        }, () => {
          this.openSnackBar("Hubo problemas al editar la tarea");
          item.text = oldText;
          item.expired = oldExpired;
        })
      }
    });
  }

  deleteItem(item: Item) {
    this.panelService.deleteItem(item).subscribe(
      () => {
        if (!this.itemsPage.last) {
          this.getItems(this.itemsPage.number, this.itemsPage.size);
        } else if (!this.itemsPage.first && this.itemsPage.numberOfElements == 1) {
          this.getItems(this.itemsPage.number - 1, this.itemsPage.size);
        } else {
          this.itemsPage.content = this.itemsPage.content.filter(obj => obj !== item);
          this.itemsPage.totalElements -= 1;
          this.itemsPage.numberOfElements -= 1;
        }
      },
      () => this.openSnackBar("Hubo problemas al borrar la tarea"));
  }

  openSnackBar(message: string) {
    this.snackBar.open(message, "Cerrar", {duration: 5000, panelClass: ['warning']});
  }
}
