package org.ismskism.springshell.commands.newapi;

import org.ismskism.springshell.model.todo.*;
import org.ismskism.springshell.service.todo.ToDoServiceImpl;
import org.ismskism.springshell.utils.ComponentHelper;
import org.ismskism.springshell.utils.ShellColor;
import org.ismskism.springshell.utils.ShellHelper;
import org.ismskism.springshell.utils.StaticProgressBar;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.table.*;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Command(group = "ToDo Manager")
public class TodoCommand {

  @Autowired
  private ToDoServiceImpl toDoService;

  @Autowired
  private ShellHelper shellHelper;

  @Autowired
  private ComponentHelper componentHelper;

  @Autowired
  private StaticProgressBar staticProgressBar;
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
  @Command(
      command = "todo add",
      description = "Add a todo to the list",
      alias = {"todo -a", "todo a"})
  public String addTodo(
      @Option(required = true)String description) {
    ToDo toDo = toDoService.createToDo(description);

    return shellHelper.getSuccessMessage(toDo.toString());
  }

  @Command(
      command = "todo list",
      description = "List all todos",
      alias = {"todo -l", "todo l"}
  )
  public void list() {
    List<ToDo> toDoList = toDoService.findAll();

    TableModel tableModel = getTableModel(toDoList);
    TableBuilder tableBuilder = getTableBuilder(tableModel);

    shellHelper.print(tableBuilder.build().render(120));
  }

  @NotNull
  private static TableBuilder getTableBuilder(TableModel tableModel) {
    TableBuilder tableBuilder = new TableBuilder(tableModel);
    tableBuilder.addHeaderBorder(BorderStyle.fancy_heavy);
    tableBuilder.addInnerBorder(BorderStyle.fancy_heavy);
    return tableBuilder;
  }

  @NotNull
  private TableModel getTableModel(List<ToDo> toDoList) {
    List<String> fieldNames = Arrays.asList("id", "description", "status", "creationDate", "modificationDate", "priority");
    List<String> headerNames = Arrays.asList("ID", "Description", "Status", "Creation Date", "Modification Date", "Priority");
    LinkedHashMap<String, Object> headers = shellHelper.createHeaders(fieldNames, headerNames);
    return new BeanListTableModel<>(toDoList, headers);
  }

  @Command(
      command = "todo status-filter",
      description = "Filter ToDos by status",
      alias = {"todo --sf", "todo sf"}
  )
  public void listByStatus() {
    String selectedStatus = componentHelper.singleSelector("Select status", shellHelper.getEnumOptionsMap(Status.class));

    List<ToDo> toDoList = toDoService.findByStatus(Status.valueOf(selectedStatus));
    TableModel tableModel = getTableModel(toDoList);
    TableBuilder tableBuilder = getTableBuilder(tableModel);

    shellHelper.print(tableBuilder.build().render(120));
  }

//  @Command(
//      command = "todo edit",
//      description = "Edit an existing ToDo",
//      alias = {"todo -e", "todo e"}
//  )
//  public String edit(
//     @Option(required = true)long id,
//     @Option String description,
//     @Option boolean completed
//  ) {
//    return toDoService.updateTodoById(id, description, completed)
//        .map(todo -> shellHelper.getSuccessMessage("Updated todo with ID: ") + todo.getId())
//        .orElse(shellHelper.getErrorMessage("Todo not found"));
//  }

  @Command(
      command = "todo update-description",
      description = "Update an existing ToDo's description",
      alias = {"todo --ud", "todo ud"}
  )
  public String updateDescription(
      @Option(required = true) long id,
      @Option(required = true) String description
  ) {
    ToDo toDo = toDoService.updateDescription(id, description);
    if (toDo != null) {
      shellHelper.getSuccessMessage(toDo.toString());
      return shellHelper.getSuccessMessage("ToDo updated on: " + toDo.getCreationDate().format(FORMATTER));
    } else {
      return shellHelper.getErrorMessage("ToDo not found with ID: " + id);
    }
  }

  @Command(
      command = "todo update-status",
      description = "Update an existing ToDo's status",
      alias = {"todo --us", "todo us"}
  )
  private String updateStatus(
      @Option(required = true) long id
  ) {
    String selectedStatus = componentHelper.singleSelector("Select status", shellHelper.getEnumOptionsMap(Status.class));

    ToDo toDo = toDoService.updateStatus(id, Status.valueOf(selectedStatus));
    return getResultString(id, toDo);
  }

  @Command(
      command = "todo update-priority",
      description = "Update an existing ToDo's priority",
      alias = {"todo -p", "todo p"}
  )
  private String updatePriority(
      @Option(required = true) long id
  ) {
    String priorityLevel = componentHelper.singleSelector("Select a priority level", shellHelper.getEnumOptionsMap(Priority.class));
    ToDo toDo = toDoService.updatePriority(id, Priority.valueOf(priorityLevel));

    return getResultString(id, toDo);
  }

  @Command(
      command = "todo update-pmi",
      description = "Update an existing ToDo's PMI value",
      alias = {"todo --pmi", "todo pmi"}
  )
  private String updatePmi(
      @Option(required = true) long id
  ) {
    String pmi = componentHelper.singleSelector("Select a PMI value", shellHelper.getEnumOptionsMap(PMI.class));
    ToDo toDo = toDoService.updatePmi(id, PMI.valueOf(pmi));

    return getResultString(id, toDo);
  }

  @Command(
      command = "todo update-score",
      description = "Update an existing ToDo's score",
      alias = {"todo --score", "todo score"}
  )
  private String updateScore(
      @Option(required = true) long id
  ) {
    String score = componentHelper.singleSelector("Select a score value", shellHelper.getEnumOptionsMap(Score.class));
    ToDo toDo = toDoService.updateScore(id, Score.valueOf(score));

    return getResultString(id, toDo);
  }


  private String getResultString(long id, ToDo toDo) {
    return toDo != null ?
        shellHelper.getSuccessMessage(toDo.toString()) :
        shellHelper.getErrorMessage("ToDo not found with ID: " + id);
  }

  @Command(
      command = "pmi --list",
      description = "Display PMI list",
      alias = {"pmil"}
  )
  public void displayPmi() {
    List<ToDo> plusList = toDoService.findByPmi(PMI.PLUS);
    List<ToDo> minusList = toDoService.findByPmi(PMI.MINUS);
    List<ToDo> interestingList = toDoService.findByPmi(PMI.INTERESTING);

    int plusScore = plusList.stream()
        .filter(todo -> todo.getScore() != null) // Ensure null safety
        .mapToInt(todo -> todo.getScore().getValue())
        .sum();
    int minusScore = minusList.stream()
        .filter(todo -> todo.getScore() != null) // Ensure null safety
        .mapToInt(todo -> todo.getScore().getValue())
        .sum();
    int interestingScore = interestingList.stream()
        .filter(todo -> todo.getScore() != null) // Ensure null safety
        .mapToInt(todo -> todo.getScore().getValue())
        .sum();
    int scoreTotal = plusScore + minusScore + interestingScore;

    // Calculate percentages
    double plusPercentage = (scoreTotal != 0) ? ((double) plusScore / scoreTotal) * 100 : 0;
    double minusPercentage = (scoreTotal != 0) ? ((double) minusScore / scoreTotal) * 100 : 0;
    double interestingPercentage = (scoreTotal != 0) ? ((double) interestingScore / scoreTotal) * 100 : 0;

    // Determine which category has the highest percentage
    double maxPercentage = Math.max(plusPercentage, Math.max(minusPercentage, interestingPercentage));

// Format the output
    String plusPercentageFormatted = String.format("%.2f%%", plusPercentage);
    String minusPercentageFormatted = String.format("%.2f%%", minusPercentage);
    String interestingPercentageFormatted = String.format("%.2f%%", interestingPercentage);

    int maxSize = Math.max(interestingList.size(), Math.max(plusList.size(), minusList.size()));
    // Create a 2D array to hold the table data
    String[][] tableData = new String[maxSize + 1][9];
    tableData[0][0] = "Id";
    tableData[0][1] = "Score";
    tableData[0][2] = "Plus";
    tableData[0][3] = "Id";
    tableData[0][4] = "Score";
    tableData[0][5] = "Minus";
    tableData[0][6] = "Id";
    tableData[0][7] = "Score";
    tableData[0][8] = "Interesting";

    // Fill the table data
    for (int i = 0; i < maxSize; i++) {
      tableData[i + 1][0] = i < plusList.size() ? plusList.get(i).getId().toString() : "";
      tableData[i + 1][1] = i < plusList.size() && plusList.get(i).getScore() != null ? String.valueOf(plusList.get(i).getScore().getValue()) : "";
      tableData[i + 1][2] = i < plusList.size() ? plusList.get(i).getDescription() : "";
      tableData[i + 1][3] = i < minusList.size() ? minusList.get(i).getId().toString() : "";
      tableData[i + 1][4] = i < minusList.size() && minusList.get(i).getScore() != null ? String.valueOf(minusList.get(i).getScore().getValue()) : "";
      tableData[i + 1][5] = i < minusList.size() ? minusList.get(i).getDescription() : "";
      tableData[i + 1][6] = i < interestingList.size() ? interestingList.get(i).getId().toString() : "";
      tableData[i + 1][7] = i < interestingList.size() && interestingList.get(i).getScore() != null ? String.valueOf(interestingList.get(i).getScore().getValue()) : "";
      tableData[i + 1][8] = i < interestingList.size() ? interestingList.get(i).getDescription() : "";
    }

    // Create the TableModel and TableBuilder
    TableModel tableModel = new ArrayTableModel(tableData);
    TableBuilder tableBuilder = new TableBuilder(tableModel);
    tableBuilder.addFullBorder(BorderStyle.fancy_light);

    shellHelper.print(tableBuilder.build().render(120));

    String plusMessage = "P: " + plusScore;
    String minusMessage = "M: " + minusScore;
    String interestingMessage = "I: " + interestingScore;
    if (plusPercentage == maxPercentage) {
      plusMessage = shellHelper.getSuccessMessage(plusMessage);
      System.out.println(plusMessage + "\t" + staticProgressBar.getProgressBar(plusPercentage, 60, ShellColor.SUCCESS));
      System.out.println(minusMessage + "\t" + staticProgressBar.getProgressBar(minusPercentage, 60, ShellColor.STANDARD));
      System.out.println(interestingMessage + "\t" + staticProgressBar.getProgressBar(interestingPercentage, 60, ShellColor.STANDARD));
    } else if (minusPercentage == maxPercentage) {
      minusMessage = shellHelper.getErrorMessage(minusMessage);
      System.out.println(plusMessage + "\t" + staticProgressBar.getProgressBar(plusPercentage, 60, ShellColor.STANDARD));
      System.out.println(minusMessage + "\t" + staticProgressBar.getProgressBar(minusPercentage, 60, ShellColor.ERROR));
      System.out.println(interestingMessage + "\t" + staticProgressBar.getProgressBar(interestingPercentage, 60, ShellColor.STANDARD));
    } else if (interestingPercentage == maxPercentage) {
      interestingMessage = shellHelper.getInfoMessage(interestingMessage);
      System.out.println(plusMessage + "\t" + staticProgressBar.getProgressBar(plusPercentage, 60, ShellColor.STANDARD));
      System.out.println(minusMessage + "\t" + staticProgressBar.getProgressBar(minusPercentage, 60, ShellColor.STANDARD));
      System.out.println(interestingMessage + "\t" + staticProgressBar.getProgressBar(interestingPercentage, 60, ShellColor.INFO));
    }
    System.out.println();
  }

  @Command(
      command = "todo complete",
      description = "Mark a ToDo as completed",
      alias = {"todo -c", "todo c"}
  )
  private String markCompletedById(
      @Option(required = true) long id
  ) {
    return toDoService.markCompletedById(id) ?
        shellHelper.getSuccessMessage("Deleted ToDO with ID: " + id) :
        shellHelper.getErrorMessage("ToDo not found with ID: " + id);

  }

  @Command(
      command = "todo delete",
      description = "Delete a todo by ID",
      alias = {"todo -d", "todo d"}
  )
  public String delete(
      @Option(required = true) long id
  ) {
    Optional<ToDo> byId = toDoService.findById(id);
    if (byId.isEmpty()) {
      return shellHelper.getErrorMessage("ToDo not found with ID: " + id);
    }

    ToDo toDo = byId.get();
    shellHelper.getWarningMessage(toDo.toString());
    boolean confirmation = componentHelper.getConditional("Are you sure you want to delete the ToDO?", false);
    return confirmation ? toDoService.deleteById(id) ?
        shellHelper.getSuccessMessage("Deleted todo with ID: " + id) :
        shellHelper.getErrorMessage("Todo not found") : shellHelper.getWarningMessage("Deletion aborted");
  }
}
