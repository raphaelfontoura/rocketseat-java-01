package com.github.raphaelfontoura.todolist.task;

public class UserNotAllowedException extends RuntimeException{

  public UserNotAllowedException(String msg) {
    super(msg);
  }
  
}
