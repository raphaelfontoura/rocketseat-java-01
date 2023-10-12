package com.github.raphaelfontoura.todolist.task;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

  private final ITaskRepository taskRepository;

  @Value("${request.filter.userid}")
  private String headerUserId;
  
  @PostMapping
  public TaskModel create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    var userId = request.getAttribute(headerUserId).toString();
    taskModel.setIdUser(UUID.fromString(userId));
    var taskCreated = taskRepository.save(taskModel);
    return taskCreated;
  }

}
