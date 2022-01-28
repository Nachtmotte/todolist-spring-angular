import { Component, OnInit, Inject } from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'app-name-dialog',
  templateUrl: './name-dialog.component.html',
  styleUrls: ['./name-dialog.component.css']
})
export class NameDialogComponent implements OnInit {

  inputData : String = this.data.inputData;

  constructor(@Inject(MAT_DIALOG_DATA)public data : any) {  }

  ngOnInit(): void {
  }

}
