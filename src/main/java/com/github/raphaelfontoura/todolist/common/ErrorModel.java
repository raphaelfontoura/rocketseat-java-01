package com.github.raphaelfontoura.todolist.common;

import java.time.Instant;


public record ErrorModel (
  int status,
  String message,
  Instant timestamp
){}
