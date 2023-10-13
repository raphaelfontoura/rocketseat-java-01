package com.github.raphaelfontoura.todolist.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private IUserRepository userRepository;


  @Test
  void postNewUserShouldReturnCreatedWithUserId() throws JsonProcessingException, Exception {
    UserModel user = new UserModel();
    user.setName("user teste");
    user.setPassword("123456");
    user.setUsername("teste");

    mockMvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(user))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists());
  }

  @Test
  void postUserShouldReturnExceptionWhenUsernameExists() throws JsonProcessingException, Exception {
    UserModel user = new UserModel();

    user.setName("user teste");
    user.setPassword("123456");
    user.setUsername("teste");
    userRepository.save(user);

    mockMvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(user))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("usuário já existe."));
  }
}
