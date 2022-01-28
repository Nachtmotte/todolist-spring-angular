import {Component, OnInit, ViewChild} from '@angular/core';

@Component({
  selector: 'app-todolist',
  templateUrl: './todolist.component.html',
  styleUrls: ['./todolist.component.css']
})
export class TodolistComponent implements OnInit {

  @ViewChild('menuTrigger') calendar: any;
  minDate: Date
  selectedDate: Date | null = null;
  text: string = "";

  constructor() {
    this.minDate = new Date();
    this.minDate.setDate(this.minDate.getDate() + 1);
  }

  ngOnInit(): void {
  }

  saveDate(event: Date) {
    this.selectedDate = event;
    this.calendar.closeMenu();
  }

  saveItem(){
    console.log("Texto: " + this.text + "\nVencimiento: " + this.selectedDate);
    this.selectedDate = null;
    this.text = "";
  }
}
