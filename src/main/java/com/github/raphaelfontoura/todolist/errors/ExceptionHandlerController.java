package com.github.raphaelfontoura.todolist.errors;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.github.raphaelfontoura.todolist.task.UserNotAllowedException;

@ControllerAdvice
public class ExceptionHandlerController {
  
  @ExceptionHandler(UserNotAllowedException.class)
  public ResponseEntity<ErrorModel> illegalAccessException(UserNotAllowedException exception) {
    var error = new ErrorModel(
    HttpStatus.FORBIDDEN.value(),
    exception.getMessage(),
    Instant.now()
    );
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }
  
  @ExceptionHandler
  public ResponseEntity<ErrorModel> badRequestError(RuntimeException exception) {
    var error = new ErrorModel(
    HttpStatus.BAD_REQUEST.value(),
    exception.getMessage(),
    Instant.now()
    );
    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorModel> illegalArgumentException(IllegalArgumentException exception) {
    var error = new ErrorModel(
    HttpStatus.BAD_REQUEST.value(),
    exception.getMessage(),
    Instant.now()
    );
    return ResponseEntity.badRequest().body(error);
  }
}
