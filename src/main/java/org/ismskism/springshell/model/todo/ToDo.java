package org.ismskism.springshell.model.todo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ToDo implements Serializable {
  private Long id;
  private String description;
  private Status status = Status.CREATED;
  private final LocalDateTime creationDate;
  private LocalDateTime modificationDate = null;
  private Priority priority = null;
  private PMI pmi = null;

  private Score score = null;

  public ToDo(String description) {
    this.description = description;
    creationDate = LocalDateTime.now();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  public LocalDateTime getModificationDate() {
    return modificationDate;
  }

  public void setModificationDate(LocalDateTime modificationDate) {
    this.modificationDate = modificationDate;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Priority getPriority() {
    return priority;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
  }

  public PMI getPmi() {
    return pmi;
  }

  public void setPmi(PMI pmi) {
    this.pmi = pmi;
  }

  public Score getScore() {
    return score;
  }

  public void setScore(Score score) {
    this.score = score;
  }

  @Override
  public String toString() {
    return "ToDo{" +
        "id=" + id +
        ", description='" + description + '\'' +
        ", status=" + status +
        ", creationDate=" + creationDate +
        ", modificationDate=" + modificationDate +
        ", priority=" + priority +
        ", pmi=" + pmi +
        ", score=" + score +
        '}';
  }
}
