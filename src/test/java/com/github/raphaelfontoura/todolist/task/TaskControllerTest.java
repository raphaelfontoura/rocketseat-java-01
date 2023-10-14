package com.github.raphaelfontoura.todolist.task;

import java.time.LocalDateTime;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.raphaelfontoura.todolist.user.UserModel;
import com.github.raphaelfontoura.todolist.user.UserService;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class TaskControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private UserService userService;

  @Autowired
  private ITaskRepository taskRepository;

  private TaskModel taskModel;

  private UserModel user;

  private String basicAuth;

  @BeforeEach
  void setUp() {
    var password = "123456";
    user = new UserModel();
    user.setName("user test");
    user.setUsername("test");
    user.setPassword(password);
    userService.saveUser(user);

    var credentials = user.getUsername() + ":" + password;
    basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

    var dateTime = LocalDateTime.now();

    taskModel = new TaskModel();
    taskModel.setTitle("Title");
    taskModel.setDescription("description test");
    taskModel.setPriority("MEDIA");
    taskModel.setStartAt(dateTime.plusHours(1));
    taskModel.setEndAt(dateTime.plusHours(2));
  }

  @Test
  void testCreate() throws JsonProcessingException, Exception {
    mockMvc.perform(post("/tasks")
        .servletPath("/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, basicAuth)
        .content(mapper.writeValueAsString(taskModel)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.idUser").value(user.getId().toString()));
  }

  @Test
  void testList() throws Exception {
    taskModel.setIdUser(user.getId());
    taskRepository.save(taskModel);

    mockMvc.perform(get("/tasks")
        .servletPath("/tasks")
        .accept(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, basicAuth))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void testUpdate() throws Exception {
    taskModel.setIdUser(user.getId());
    taskRepository.save(taskModel);
    var updateTask = new TaskModel();
    updateTask.setTitle("new title");

    mockMvc.perform(put("/tasks/{idTask}", taskModel.getId().toString())
        .servletPath("/tasks/"+taskModel.getId().toString())
        .accept(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, basicAuth)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(updateTask)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value(updateTask.getTitle()))
        .andExpect(jsonPath("$.description").value(taskModel.getDescription()))
        .andExpect(jsonPath("$.idUser").value(user.getId().toString()));
  }

  @Test
  void testUpdateShouldReturnForbiddenWhenUserNotAllowed() throws Exception {
    taskModel.setIdUser(user.getId());
    taskRepository.save(taskModel);
    var updateTask = new TaskModel();
    updateTask.setTitle("new title");
    var otherBasicAuth = getBasicAuthorizationFromOtherUser();

    mockMvc.perform(put("/tasks/{idTask}", taskModel.getId().toString())
        .servletPath("/tasks/"+taskModel.getId().toString())
        .accept(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, otherBasicAuth)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(updateTask)))
        .andExpect(status().isForbidden());
  }

  private String getBasicAuthorizationFromOtherUser() {
    var password = "123456";
    user = new UserModel();
    user.setName("other user");
    user.setUsername("other");
    user.setPassword(password);
    userService.saveUser(user);

    var credentials = user.getUsername() + ":" + password;
    return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
  }
}
