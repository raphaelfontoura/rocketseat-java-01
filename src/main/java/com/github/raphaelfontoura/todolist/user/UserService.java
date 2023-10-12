package com.github.raphaelfontoura.todolist.user;

import org.springframework.stereotype.Service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.RequiredArgsConstructor;

/**
 * UserService
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final IUserRepository repository;

  public UserModel saveUser(UserModel userModel) {
    if (repository.findByUsername(userModel.getUsername()) != null) {
      System.out.println("Usuário existente");
      throw new IllegalArgumentException("usuário já existe.");
    }
    var hashedPassword = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
    userModel.setPassword(hashedPassword);
    var userCreated = repository.save(userModel);
    return userCreated;
  }
  
}
                                                                                                                                                                     