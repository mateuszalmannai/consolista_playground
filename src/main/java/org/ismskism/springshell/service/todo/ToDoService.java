package org.ismskism.springshell.service.todo;

import org.ismskism.springshell.model.todo.*;

import java.util.List;
import java.util.Optional;

public interface ToDoService {
  ToDo createToDo(String description);
  ToDo updateDescription(Long id, String description);
  ToDo updateStatus(Long id, Status status);
  boolean markCompletedById(Long id);
  boolean deleteToDo(ToDo toDo);
  boolean deleteById(Long id);
  List<ToDo> findAll();
  List<ToDo> findByStatus(Status status);
  Optional<ToDo> findById(Long id);
  List<ToDo> findByIds(List<Long> idList);
  ToDo updatePriority(Long id, Priority priority);
  ToDo updatePmi(Long id, PMI pmi);
  List<ToDo> findByPmi(PMI pmi);
  ToDo updateScore(Long id, Score score);
}
