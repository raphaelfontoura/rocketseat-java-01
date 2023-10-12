package com.github.raphaelfontoura.todolist.task;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.raphaelfontoura.todolist.common.ErrorModel;
import com.github.raphaelfontoura.todolist.utils.BeanUpdateUtil;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

  private final ITaskRepository taskRepository;

  @Value("${requestparam.userid}")
  private String headerUserId;
  
  @PostMapping
  public TaskModel create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    var userId = request.getAttribute(headerUserId).toString();
    taskModel.setIdUser(UUID.fromString(userId));
    validateTaskModel(taskModel);
    var taskCreated = taskRepository.save(taskModel);
    return taskCreated;
  }

  @GetMapping
  public List<TaskModel> list(HttpServletRequest request) {
    var userId = request.getAttribute(headerUserId).toString();
    var tasks = taskRepository.findByIdUser(UUID.fromString(userId));
    return tasks;
  }

  @PutMapping("/{id}")
  public TaskModel update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {
    var taskEntity = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task não encontrada. Verifique."));
    BeanUpdateUtil.updateBean(taskModel, taskEntity);
    return taskRepository.save(taskEntity);
  }

  private void validateTaskModel(TaskModel taskModel) {
    var currentDate = LocalDateTime.now();
    if (currentDate.isAfter(taskModel.getStartAt())) throw new IllegalArgumentException("Data de início deve ser maior que a data atual");
    if (taskModel.getEndAt().isBefore(taskModel.getStartAt())) throw new IllegalArgumentException("Data final deve ser maior que a data de início");
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

}
