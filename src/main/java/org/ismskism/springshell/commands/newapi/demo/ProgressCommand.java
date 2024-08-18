package org.ismskism.springshell.commands.newapi.demo;

import org.ismskism.springshell.utils.ProgressBar;
import org.ismskism.springshell.utils.ProgressCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;

@Command(group = "Progress")
public class ProgressCommand {

  @Autowired
  ProgressCounter progressCounter;

  @Autowired
  ProgressBar progressBar;
  @Command(command = "progress-spinner", description = "Displays progress spinner", group = "Progress")
  public void progressSpinner() throws InterruptedException {
    for (int i = 1; i <100; i++) {
      progressCounter.display();
      Thread.sleep(100);
    }
    progressCounter.reset();
  }

  @Command(command = "progress-counter", description = "Displays progress counter (with spinner)", group = "Progress")
  public void progressCounter() throws InterruptedException{
    for (int i = 0; i < 100; i++) {
      progressCounter.display(i, "Processing");
      Thread.sleep(100);
    }
    progressCounter.reset();
  }

  @Command(command = "progress-bar", description = "Display progress bar", group = "Progress")
  public void progressBar() throws InterruptedException {
    for (int i = 0; i < 100; i++) {
      progressBar.display(i);
      Thread.sleep(100);
    }
    progressBar.reset();
  }
}
