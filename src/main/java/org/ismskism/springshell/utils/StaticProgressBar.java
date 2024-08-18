package org.ismskism.springshell.utils;

public class StaticProgressBar {
  private static final String DONE_MARKER = "=";
  private static final String REMAINS_MARKER = "-";
  private static final String LEFT_DELIMITER = "[";
  private static final String RIGHT_DELIMITER = "]";

  private final ShellHelper shellHelper;

  public StaticProgressBar(ShellHelper shellHelper) {
    this.shellHelper = shellHelper;
  }

  public String getProgressBar(double percentage, int barLength, ShellColor shellColor) {
    return getProgressBar(percentage, null, barLength, shellColor);
  }

  public String getProgressBar(double percentage, String statusMessage, int barLength, ShellColor shellColor) {
    int doneLength = (int) ((percentage / 100) * barLength);
    int remainsLength = barLength - doneLength;
    String message = (statusMessage == null) ? "" : statusMessage;

    String done = "";
    switch (shellColor) {
      case INFO -> done = shellHelper.getInfoMessage(new String(new char[doneLength]).replace("\0", DONE_MARKER));
      case SUCCESS -> done = shellHelper.getSuccessMessage(new String(new char[doneLength]).replace("\0", DONE_MARKER));
      case WARNING -> done = shellHelper.getWarningMessage(new String(new char[doneLength]).replace("\0", DONE_MARKER));
      case ERROR -> done = shellHelper.getErrorMessage(new String(new char[doneLength]).replace("\0", DONE_MARKER));
      case STANDARD -> done = new String(new char[doneLength]).replace("\0", DONE_MARKER);
    }


    String remains = new String(new char[remainsLength]).replace("\0", REMAINS_MARKER);

    return String.format("%s%s%s%s %.2f%% %s", LEFT_DELIMITER, done, remains, RIGHT_DELIMITER, percentage, message);
  }
}
