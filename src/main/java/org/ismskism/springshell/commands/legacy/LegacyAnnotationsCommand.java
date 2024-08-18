package org.ismskism.springshell.commands.legacy;

import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class LegacyAnnotationsCommand {

  @ShellMethod(value = "Demonstration of the legacy API", key = "legacy", group = "Demo")
  public String legacyTest() {
    return "Testing the legacy annotations";
  }
}
