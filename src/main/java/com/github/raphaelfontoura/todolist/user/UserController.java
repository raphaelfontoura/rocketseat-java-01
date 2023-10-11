package com.github.raphaelfontoura.todolist.user;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final IUserRepository userRepository;

  @PostMapping
  public ResponseEntity<UserModel> create(@RequestBody UserModel userModel) {
    var user = userRepository.findByUsername(userModel.getUsername());
    if (user != null) {
      System.out.println("Usuário existente");
      // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe");
      throw new IllegalArgumentException("usuário já existe.");
    };
    var userCreated = userRepository.save(userModel);
    var uri = UriComponentsBuilder.newInstance()
      .scheme("http")
      .host("localhost")
      .path("/users/" + userCreated.getId())
      .build().toUri();
    return ResponseEntity.created(uri).body(userCreated);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorModel> badRequestError(IllegalArgumentException exception) {
    var error = new ErrorModel();
    error.setStatus(HttpStatus.BAD_REQUEST.value());
    error.setMessage(exception.getMessage());
    error.setTimestamp(Instant.now());
    return ResponseEntity.badRequest().body(error);
  }
  
}

@Data
class ErrorModel {
  private int status;
  private String message;
  private Instant timestamp;
}
