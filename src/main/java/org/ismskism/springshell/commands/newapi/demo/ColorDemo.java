package org.ismskism.springshell.commands.newapi.demo;

import org.ismskism.springshell.utils.ShellHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.context.InteractionMode;

@Command(group = "Demo")
public class ColorDemo {

  @Autowired
  ShellHelper shellHelper;

  @Command(command = "echo", description = "Displays greeting message to the user who's name is supplied", interactionMode = InteractionMode.NONINTERACTIVE, group = "Colours")
  public String echoCommand(@Option(longNames = {"-N", "--name"})String name) {
    String message = String.format("Hello %s!", name);
    shellHelper.print(message.concat(" (Default style message)"));
    shellHelper.printError(message.concat(" (Error style message)"));
    shellHelper.printWarning(message.concat(" (Warning style message)"));
    shellHelper.printInfo(message.concat(" (Info style message)"));
    shellHelper.printSuccess(message.concat(" (Success style message)"));
    String output = shellHelper.getSuccessMessage(message);
    return output.concat(" You are running spring shell cli-demo.");
  }


}
