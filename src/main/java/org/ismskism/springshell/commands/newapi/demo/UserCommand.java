package org.ismskism.springshell.commands.newapi.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ismskism.springshell.model.demo.CliUser;
import org.ismskism.springshell.model.demo.Gender;
import org.ismskism.springshell.service.demo.UserService;
import org.ismskism.springshell.utils.BeanTableModelBuilder;
import org.ismskism.springshell.utils.ComponentHelper;
import org.ismskism.springshell.utils.InputReader;
import org.ismskism.springshell.utils.ShellHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.table.*;
import org.springframework.util.StringUtils;

import java.util.*;

@Command(group = "User Command")
public class UserCommand {

  @Autowired
  ShellHelper shellHelper;

  @Autowired
  UserService userService;

  @Autowired
  InputReader inputReader;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  ComponentHelper componentHelper;
  @Command(command = "user update-all", description = "Update and synchronise all users in local database with external source")
  @CommandAvailability(provider = {"isUserLoggedIn", "isUserAdmin"})
  public void updateAllUsers() {
    shellHelper.printInfo("Starting local user db update");
    long numOfUsers = userService.updateAll();
    String successMessage = shellHelper.getSuccessMessage("SUCCESS >>");
    successMessage = successMessage + String.format(" Total of %d local db users updated!", numOfUsers);
    shellHelper.print(successMessage);
  }

  @Command(command = "user create", description = "Demonstrate user prompts.")
  @CommandAvailability(provider = {"isUserLoggedIn", "isUserAdmin"})
  public void createUser(@Option(longNames = {"-U", "--username"})String username) {
    if (userService.exists(username)) {
      shellHelper.printError(String.format("User with username='%s' already exists --> ABORTING", username));
      return;
    }
    CliUser user = new CliUser();
    user.setUsername(username);

    // 1. read user's fullName --------------------------------------------
    do {
      String fullName = componentHelper.stringInput("Full name:", null, false);
      if (StringUtils.hasText(fullName)) {
        user.setFullName(fullName);
      } else {
        shellHelper.printWarning("User's full name CAN NOT be empty string? Please enter valid value!");
      }
    } while (user.getFullName() == null);

    // 2. read user's password --------------------------------------------
    do {
      String password = componentHelper.stringInput("Password:", null, true);
      if (StringUtils.hasText(password)) {
        user.setPassword(password);
      } else {
        shellHelper.printWarning("Password CAN NOT be empty string! Please enter valid value!");
      }
    } while (user.getPassword() == null);

//    // 3. read user's Gender ----------------------------------------------
    Map<String, String> options = new HashMap<>();
    options.put("[M] " + Gender.MALE.name(), Gender.MALE.name());
    options.put("[F] " + Gender.FEMALE.name(),  Gender.FEMALE.name() );
    options.put("[D] " + Gender.DIVERSE.name(), Gender.DIVERSE.name());

    String genderSelection = componentHelper.singleSelector("Gender", options);
    Gender gender = Gender.valueOf(genderSelection);
    user.setGender(gender);

    // 4. Prompt for superuser attribute ------------------------------
    boolean isSuperUser = componentHelper.getConditional("Is new user superuser?", false);
    user.setSuperuser(isSuperUser);
    // Print user's input -------------------------------------------------
    shellHelper.printInfo("\nCreating new user:");
    displayUser(user);

    // 5. Prompt user for confirmation of values -----------------------
    boolean isConfirmed = componentHelper.getConditional("Confirm values for user creation", false);
      if (isConfirmed) {
      CliUser createdUser = userService.create(user);
      shellHelper.printSuccess("Created user with id=" + createdUser.getId());
    } else {
      shellHelper.printInfo("No user created.");
    }
  }

  @Command(command = "user list", description = "Display list of users")
  @CommandAvailability(provider = {"isUserLoggedIn", "isUserAdmin"})
  public void userList() {
    List<CliUser> users = userService.findAll();

    LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
    headers.put("id", "Id");
    headers.put("username", "Username");
    headers.put("fullName", "Full name");
    headers.put("gender", "Gender");
    headers.put("superuser", "Superuser");
    TableModel model = new BeanListTableModel<>(users, headers);

    TableBuilder tableBuilder = new TableBuilder(model);
    tableBuilder.addInnerBorder(BorderStyle.fancy_light);
    tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
    shellHelper.print(
        tableBuilder.build()
        .render(80));
  }

  @Command(command = "user details", description = "Display details of user with supplied username")
  @CommandAvailability(provider = {"isUserLoggedIn", "isUserAdmin"})
  public void userDetails(@Option(longNames = {"-U", "--username"}) String username) {
    CliUser user = userService.findByUsername(username);
    if (user == null) {
      shellHelper.printWarning("No user with the supplied username could be found?!");
      return;
    }
    displayUser(user);
  }


  @Command(command = "user my-details", description = "Display details of signed-in user")
  @CommandAvailability(provider = {"isUserLoggedIn"})
  public void myDetails() {
    CliUser user = userService.getLoggedInUser();
    if (user == null) {
      shellHelper.printWarning("No user with the supplied username could be found!");
      return;
    }
    displayUser(user);
  }



  private void displayUser(CliUser user) {
    LinkedHashMap<String, Object> labels = new LinkedHashMap<>();
    labels.put("id", "Id");
    labels.put("username", "Username");
    labels.put("fullName", "Full name");
    labels.put("gender", "Gender");
    labels.put("superuser", "Superuser");
    labels.put("password", "Password");

    String[] header = new String[] {"Property", "Value"};
    BeanTableModelBuilder builder = new BeanTableModelBuilder(user, objectMapper);
    TableModel model = builder.withLabels(labels).withHeader(header).build();

    TableBuilder tableBuilder = new TableBuilder(model);

    tableBuilder.addInnerBorder(BorderStyle.fancy_light);
    tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
    tableBuilder.on(CellMatchers.column(0)).addSizer(new AbsoluteWidthSizeConstraints(20));
    tableBuilder.on(CellMatchers.column(1)).addSizer(new AbsoluteWidthSizeConstraints(30));
    shellHelper.print(tableBuilder.build().render(80));
  }
}
