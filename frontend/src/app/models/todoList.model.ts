export class TodoList{
  id: number;
  name: string;
  created: Date;

  constructor(id: number, name: string, created: Date) {
    this.id = id;
    this.name = name;
    this.created = created;
  }
}

export class GetTodoLists {
  lists: TodoList[];

  constructor(lists: TodoList[]) {
    this.lists = lists;
  }
}

export class GetTodoList {
  list: TodoList;

  constructor(list: TodoList) {
    this.list = list;
  }
}
