package org.ismskism.springshell.commands.newapi.demo;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.component.ViewComponent;
import org.springframework.shell.component.view.TerminalUI;
import org.springframework.shell.component.view.control.BoxView;
import org.springframework.shell.component.view.control.InputView;
import org.springframework.shell.component.view.control.ProgressView;
import org.springframework.shell.geom.HorizontalAlign;
import org.springframework.shell.geom.VerticalAlign;
import org.springframework.shell.standard.AbstractShellComponent;

import java.util.ArrayList;

@Command(group = "UI Commands")
public class ComponentUiCommands extends AbstractShellComponent {
  @Command(command = "componentui tui1")
  public void tui1() {
    TerminalUI ui = new TerminalUI(getTerminal());
    BoxView view = new BoxView();
    view.setShowBorder(true);
    view.setDrawFunction((screen, rect) -> {
      screen.writerBuilder()
          .build()
          .text("Hello World", rect, HorizontalAlign.CENTER, VerticalAlign.CENTER);
      return rect;
    });
    ui.setRoot(view, true);
    ui.run();
  }

  @Command(command = "componentui tui2")
  public void tui2() {
    TerminalUI ui = new TerminalUI(getTerminal());
    BoxView view = new BoxView();
    view.setRect(0, 0, 40, 5);
    view.setShowBorder(true);
    view.setDrawFunction((screen, rect) -> {
      screen.writerBuilder()
          .build()
          .text("Hello World", rect, HorizontalAlign.CENTER, VerticalAlign.CENTER);
      return rect;
    });
    ui.setRoot(view, false);
    ui.run();
  }

  @Command(command = "componentui tui3")
  public void tui3() {
    TerminalUI ui = new TerminalUI(getTerminal());
    BoxView view = new BoxView();
    view.setShowBorder(true);
    view.setDrawFunction((screen, rect) -> {
      screen.writerBuilder()
          .build()
          .text("Hello World", rect, HorizontalAlign.CENTER, VerticalAlign.CENTER);
      return rect;
    });
    ui.setRoot(view, false);
    ui.run();
  }

  @Command(command = "componentui string")
  public String stringInput() {
    InputView view = new InputView();
    view.setRect(0, 0, 10, 1);
    ViewComponent component = getViewComponentBuilder().build(view);
    component.runBlocking();
    String input = view.getInputText();
    return String.format("Input was '%s'", input);
  }

  @Command(command = "componentui progress", alias = "prog")
  public void progress(
      @Option(defaultValue = "desc") String description,
      @Option(defaultValue = "true") boolean textEnabled,
      @Option(defaultValue = "true") boolean spinnerEnabled,
      @Option(defaultValue = "true") boolean percentEnabled,
      @Option(defaultValue = "0") int textSize,
      @Option(defaultValue = "0") int spinnerSize,
      @Option(defaultValue = "0") int percentSize,
      @Option(defaultValue = "CENTER") HorizontalAlign textAlign,
      @Option(defaultValue = "CENTER") HorizontalAlign spinnerAlign,
      @Option(defaultValue = "CENTER") HorizontalAlign percentAlign,
      @Option(defaultValue = "-1") int logMessagesRate,
      @Option(defaultValue = "200") long advanceSleep,
      @Option(defaultValue = "false") boolean logMessagesSleep
  ) {
    ArrayList<ProgressView.ProgressViewItem> items = new ArrayList<>();
    if (textEnabled) {
      items.add(ProgressView.ProgressViewItem.ofText(textSize, textAlign));
    }
    if (spinnerEnabled) {
      items.add(ProgressView.ProgressViewItem.ofSpinner(spinnerSize, spinnerAlign));
    }
    if (percentEnabled) {
      items.add(ProgressView.ProgressViewItem.ofPercent(percentSize, percentAlign));
    }
    ProgressView.ProgressViewItem[] itemsArray = items.toArray(new ProgressView.ProgressViewItem[0]);
    ProgressView view = new ProgressView(itemsArray);
    view.setDescription(description);
    view.setRect(0, 0, 20, 1);

    ViewComponent component = getViewComponentBuilder().build(view);
    view.start();

    ViewComponent.ViewComponentRun run = component.runAsync();

    for (int i = 0; i < 51; i++) {
      if (run.isDone()) {
        break;
      }
      sleep(advanceSleep);
      if (run.isDone()) {
        break;
      }
      if (logMessagesRate > 0 && (i % logMessagesRate) == 0) {
        int width = getTerminal().getWidth();
        String msg = String.format("%-" + width + "s", i);
        getTerminal().writer().write(msg + System.lineSeparator());
        getTerminal().writer().flush();
      }
      view.tickAdvance(1);
    }

    if (logMessagesSleep) {
      view.stop();
      sleep(2000);
      view.start();
    }

    for (int i = 51; i < 101; i++) {
      if (run.isDone()) {
        break;
      }
      sleep(advanceSleep);
      if (run.isDone()) {
        break;
      }
      if (logMessagesRate > 0 && (i % logMessagesRate) == 0) {
        int width = getTerminal().getWidth();
        String msg = String.format("%-" + width + "s", i);
        getTerminal().writer().write(msg + System.lineSeparator());
        getTerminal().writer().flush();
      }
      view.tickAdvance(1);
    }

    view.stop();
    run.cancel();
  }

  private static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
    }
  }
}
