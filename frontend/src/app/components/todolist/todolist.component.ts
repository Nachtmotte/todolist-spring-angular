import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {TodoList} from "../../models/todoList.model";
import {Item} from "../../models/item.model";

@Component({
  selector: 'app-todolist',
  templateUrl: './todolist.component.html',
  styleUrls: ['./todolist.component.css']
})
export class TodolistComponent implements OnInit {

  @Input() folder: TodoList | null = null;
  @Output() updateFolderEvent: EventEmitter<void>;
  @Output() deleteFolderEvent: EventEmitter<void>;
  @ViewChild('menuTrigger') calendar: any;
  minDate: Date = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);
  selectedDate: Date | null = null;
  text: string = "";
  newItem: Item | null = null;
  updatePanels: any = {unchecked: true, checked: true, expired: true};

  constructor() {
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
    this.newItem = new Item(null, this.text, false, null, this.selectedDate, this.folder?.id);
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
    this.sendFolderUpdateEvent();
  }

  deleteFolder() {
    this.sendFolderDeleteEvent();
  }
}
