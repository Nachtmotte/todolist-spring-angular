import { Component, OnInit, Inject } from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.css']
})
export class DialogComponent implements OnInit {

  inputData : String = this.data.inputData;
  inputDate : Date | null = this.data.inputDate == null? null : new Date(this.data.inputDate);
  minDate: Date = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);

  constructor(@Inject(MAT_DIALOG_DATA)public data : any) {  }

  ngOnInit(): void {
  }

}
