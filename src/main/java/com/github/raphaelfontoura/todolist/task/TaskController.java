package com.github.raphaelfontoura.todolist.task;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.raphaelfontoura.todolist.common.ErrorModel;

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
    validateTaskModel(taskModel);
    var taskCreated = taskRepository.save(taskModel);
    return taskCreated;
  }

  private void validateTaskModel(TaskModel taskModel) {
    var currentDate = LocalDateTime.now();
    if (currentDate.isAfter(taskModel.getStartAt())) throw new IllegalArgumentException("Data de início deve ser maior que a data atual");
    if (taskModel.getEndAt().isBefore(taskModel.getStartAt())) throw new IllegalArgumentException("Data final deve ser maior que a data de início");
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorModel> badRequestError(IllegalArgumentException exception) {
    var error = new ErrorModel(
    HttpStatus.BAD_REQUEST.value(),
    exception.getMessage(),
    Instant.now()
    );
    return ResponseEntity.badRequest().body(error);
  }

}
