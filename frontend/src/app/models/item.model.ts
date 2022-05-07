export class Item {
  id: number | null;
  text: string;
  state: boolean;
  created: Date | null;
  expired: Date | null;
  todoListId: number | null;

  constructor(id: number | null = null, text: string = "", state: boolean = false,
              created: Date | null = null, expired: Date | null = null, todoListId: number | null = null) {
    this.id = id;
    this.text = text;
    this.state = state;
    this.created = created;
    this.expired = expired;
    this.todoListId = todoListId;
  }
}

export class GetItem{
  item: Item;

  constructor(item: Item) {
    this.item = item
  }
}
