package com.github.raphaelfontoura.todolist.errors;

import java.time.Instant;


public record ErrorModel (
  int status,
  String message,
  Instant timestamp
){}
