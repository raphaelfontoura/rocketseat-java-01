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

import com.github.raphaelfontoura.todolist.errors.ErrorModel;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<UserModel> create(@RequestBody UserModel userModel) {
    var userCreated = userService.saveUser(userModel);
    var uri = UriComponentsBuilder.newInstance()
      .scheme("http")
      .host("localhost")
      .path("/users/" + userCreated.getId())
      .build().toUri();
    return ResponseEntity.created(uri).body(userCreated);
  }

  
}
