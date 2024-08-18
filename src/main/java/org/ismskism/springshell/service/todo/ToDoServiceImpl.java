package org.ismskism.springshell.service.todo;

import org.ismskism.springshell.model.todo.*;
import org.mapdb.Atomic;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class ToDoServiceImpl implements ToDoService{
  private static final String TODO_MAPDB = "todo.mapdb";
  private static final String TODO = "todo";
  private DB db = null;
  private ConcurrentMap<Long, ToDo> map = null;
  private final Comparator<ToDo> toDoComparator = Comparator.comparing(ToDo::getCreationDate);

  private void start() {
    db = DBMaker.fileDB(TODO_MAPDB).make();
    map = db.hashMap(TODO, Serializer.LONG, Serializer.JAVA).createOrOpen();
  }

  private void shutdown() {
    db.close();
  }

  private void executeWithStartAndShutdown(Runnable logic) {
    start();
    try {
      logic.run();
    } finally {
      shutdown();
    }
  }

  private <T> T executeWithStartAndShutdown(Supplier<T> logic) {
    start();
    try {
      return logic.get();
    } finally {
      shutdown();
    }
  }
  @Override
  public ToDo createToDo(String description) {
    return executeWithStartAndShutdown(() -> {
      ToDo toDo = new ToDo(description);
      toDo.setId(getNewTaskId());
      map.put(toDo.getId(), toDo);
      return toDo;
    });
  }

  private Long getNewTaskId() {
    Atomic.Long id = db.atomicLong("id").createOrOpen();
    return id.addAndGet(1);
  }

  @Override
  public ToDo updateDescription(Long id, String description) {
    Optional<ToDo> byId = findById(id);
    if (byId.isPresent()) {
      ToDo toDo = byId.get();
      toDo.setDescription(description);
      toDo.setModificationDate(LocalDateTime.now());

      executeWithStartAndShutdown(() -> map.put(toDo.getId(), toDo));

      return toDo;
    }
    return null;
  }

  @Override
  public ToDo updatePriority(Long id, Priority priority) {
    Optional<ToDo> byId = findById(id);

    if (byId.isPresent()) {
      ToDo toDo = byId.get();
      toDo.setPriority(priority);
      toDo.setModificationDate(LocalDateTime.now());

      executeWithStartAndShutdown(() -> map.put(toDo.getId(), toDo));

      return toDo;
    }

    return null;
  }

  @Override
  public ToDo updatePmi(Long id, PMI pmi) {
    Optional<ToDo> byId = findById(id);

    if (byId.isPresent()) {
      ToDo toDo = byId.get();
      toDo.setPmi(pmi);
      toDo.setModificationDate(LocalDateTime.now());

      executeWithStartAndShutdown(() -> map.put(toDo.getId(), toDo));

      return toDo;
    }
    return null;
  }

  @Override
  public List<ToDo> findByPmi(PMI pmi) {
    return findAll()
        .stream()
        .filter(todo -> todo.getPmi() == pmi)
        .toList();
  }

  @Override
  public ToDo updateScore(Long id, Score score) {
    Optional<ToDo> byId = findById(id);

    if (byId.isPresent()) {
      ToDo toDo = byId.get();
      toDo.setScore(score);
      toDo.setModificationDate(LocalDateTime.now());

      executeWithStartAndShutdown(() -> map.put(toDo.getId(), toDo));

      return toDo;
    }
    return null;
  }

  @Override
  public ToDo updateStatus(Long id, Status status) {
    Optional<ToDo> byId = findById(id);

    if (byId.isPresent()) {
      ToDo toDo = byId.get();
      toDo.setStatus(status);
      toDo.setModificationDate(LocalDateTime.now());

      executeWithStartAndShutdown(() -> map.put(toDo.getId(), toDo));

      return toDo;
    }

    return null;
  }

  @Override
  public boolean markCompletedById(Long id) {
    Optional<ToDo> byId = findById(id);
    if (byId.isPresent()) {
      ToDo updatedToDo = updateStatus(id, Status.COMPLETED);
      return Objects.nonNull(updatedToDo);
    }
    return false;
  }

  @Override
  public boolean deleteToDo(ToDo toDo) {
    if (Objects.nonNull(toDo.getId())) {
      executeWithStartAndShutdown(() -> map.remove(toDo.getId()));
    }
    return true;
  }

  @Override
  public boolean deleteById(Long id) {
    if (Objects.nonNull(id)) {
      executeWithStartAndShutdown(() -> map.remove(id));
    }
    return true;
  }

  @Override
  public List<ToDo> findAll() {
    start();
    List<ToDo> collected = map.values()
        .stream()
        .sorted(toDoComparator)
        .collect(Collectors.toList());
    shutdown();
    return collected;
  }

  @Override
  public List<ToDo> findByStatus(Status status) {
    return findAll()
        .stream()
        .filter(todo -> todo.getStatus() == status)
        .toList();
  }

  @Override
  public Optional<ToDo> findById(Long id) {
    return executeWithStartAndShutdown(() -> Optional.ofNullable(map.get(id)));
  }

  @Override
  public List<ToDo> findByIds(List<Long> idList) {
    return executeWithStartAndShutdown(() -> idList.stream()
        .map(map::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList()));
//    start();
//    List<ToDo> collected = idList.stream()
//        .map(map::get)
//        .filter(Objects::nonNull)
//        .toList();
//    shutdown();
//
//    return collected;
  }

}
