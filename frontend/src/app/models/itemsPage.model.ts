import {Item} from "./item.model";

export class ItemsPage{
  first: boolean;
  last: boolean;
  number: number;
  numberOfElements: number;
  size: number;
  totalPages: number;
  totalElements: number;
  content: Item[];


  constructor(first: boolean, last: boolean, number: number, numberOfElements: number,
              size: number, totalPages: number, totalElements: number, content: Item[]) {
    this.first = first;
    this.last = last;
    this.number = number;
    this.numberOfElements = numberOfElements;
    this.size = size;
    this.totalPages = totalPages;
    this.totalElements = totalElements;
    this.content = content;
  }
}
