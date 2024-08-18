package org.ismskism.springshell.utils;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Value;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShellHelper {
  @Value("${shell.out.info}")
  public String infoColor;
  @Value("${shell.out.success}")
  public String successColor;
  @Value("${shell.out.warning}")
  public String warningColor;
  @Value("${shell.out.error}")
  public String errorColor;
  @Value("${shell.out.standard}")
  public String standardColor;
  private Terminal terminal;
  public ShellHelper(Terminal terminal) {
    this.terminal = terminal;
  }

  public String getColored(String message, PromptColor color) {
    return (new AttributedStringBuilder()).append(message, AttributedStyle.DEFAULT.foreground(color.toJlineAttributedStyle())).toAnsi();
  }
  public String getInfoMessage(String message) {
    return getColored(message, PromptColor.valueOf(infoColor));
  }
  public String getSuccessMessage(String message) {
    return getColored(message, PromptColor.valueOf(successColor));
  }
  public String getWarningMessage(String message) {
    return getColored(message, PromptColor.valueOf(warningColor));
  }
  public String getErrorMessage(String message) {
    return getColored(message, PromptColor.valueOf(errorColor));
  }

  public String getStandardMessage(String message) {
    return getColored(message, PromptColor.valueOf(standardColor));
  }

  public LinkedHashMap<String, Object> createHeaders(List<String> fieldNames, List<String> headerNames) {
    if (fieldNames.size() != headerNames.size()) {
      throw new IllegalArgumentException("Field names and header names lists must have the same size");
    }

    LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
    for (int i = 0; i < fieldNames.size(); i++) {
      headers.put(fieldNames.get(i), headerNames.get(i));
    }
    return headers;
  }

  public <E extends Enum<E>> Map<String, String> getEnumOptionsMap(Class<E> enumClass) {
    Map<String, String> options = new LinkedHashMap<>();
    E[] values = enumClass.getEnumConstants();
    for (E value : values) {
      options.put(value.name(), value.name());
    }
    return options;
  }
  /**
   * Print message to the console in the default color.
   *
   * @param message message to print
   */
  public void print(String message) {
    print(message, null);
  }
  /**
   * Print message to the console in the success color.
   *
   * @param message message to print
   */
  public void printSuccess(String message) {
    print(message, PromptColor.valueOf(successColor));
  }
  /**
   * Print message to the console in the info color.
   *
   * @param message message to print
   */
  public void printInfo(String message) {
    print(message, PromptColor.valueOf(infoColor));
  }
  /**
   * Print message to the console in the warning color.
   *
   * @param message message to print
   */
  public void printWarning(String message) {
    print(message, PromptColor.valueOf(warningColor));
  }
  /**
   * Print message to the console in the error color.
   *
   * @param message message to print
   */
  public void printError(String message) {
    print(message, PromptColor.valueOf(errorColor));
  }
  /**
   * Generic Print to the console method.
   *
   * @param message message to print
   * @param color   (optional) prompt color
   */
  public void print(String message, PromptColor color) {
    String toPrint = message;
    if (color != null) {
      toPrint = getColored(message, color);
    }
    terminal.writer().println(toPrint);
    terminal.flush();
  }

  public Terminal getTerminal() {
    return terminal;
  }
}
