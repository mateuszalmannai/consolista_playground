package org.ismskism.springshell.commands.newapi.demo;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.style.FigureSettings;
import org.springframework.shell.style.StyleSettings;
import org.springframework.shell.style.ThemeResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Command(group = "Styles")
public class ThemeCommand {
  List<String> colorGround = Arrays.asList("fg", "bg");
  List<String> colors = Arrays.asList("black", "red", "green", "yellow", "blue", "magenta", "cyan", "white");
  List<String> named = Arrays.asList("default", "bold", "faint", "italic", "underline", "blink", "inverse",
      "inverseneg", "conceal", "crossedout", "hidden");
  List<String> rgbRedHue = Arrays.asList("#ff0000", "#ff4000", "#ff8000", "#ffbf00", "#ffff00", "#bfff00", "#80ff00",
      "#40ff00", "#00ff00", "#00ff40", "#00ff80", "#00ffbf", "#00ffff", "#00bfff", "#0080ff", "#0040ff",
      "#0000ff", "#4000ff", "#8000ff", "#bf00ff", "#ff00ff", "#ff00bf", "#ff0080", "#ff0040", "#ff0000");
  List<String> themeTags = Arrays.asList(StyleSettings.TAG_TITLE, StyleSettings.TAG_VALUE, StyleSettings.TAG_LIST_KEY,
      StyleSettings.TAG_LIST_VALUE, StyleSettings.TAG_LEVEL_INFO, StyleSettings.TAG_LEVEL_WARN,
      StyleSettings.TAG_LEVEL_ERROR, StyleSettings.TAG_ITEM_ENABLED, StyleSettings.TAG_ITEM_DISABLED,
      StyleSettings.TAG_ITEM_SELECTED, StyleSettings.TAG_ITEM_UNSELECTED, StyleSettings.TAG_ITEM_SELECTOR);

  @Autowired
  private ThemeResolver themeResolver;

  @Command(command = "theme showcase values", description = "Showcase colors and styles")
  public AttributedString showcaseValues() {
    AttributedStringBuilder builder = new AttributedStringBuilder();
    combinations1().stream()
        .forEach(spec -> {
          AttributedStyle style = themeResolver.resolveStyle(spec);
          AttributedString styledStr = new AttributedString(spec, style);
          builder.append(String.format("%-25s", spec));
          builder.append(" ");
          builder.append(styledStr);
          builder.append("\n");
        });
    return builder.toAttributedString();
  }

  @Command(command = "theme showcase rgb", description = "Showcase colours and styles with rgb")
  public AttributedString showcaseRgb() {
    AttributedStringBuilder builder = new AttributedStringBuilder();
    combinations2().stream()
        .forEach(spec -> {
          AttributedStyle style = themeResolver.resolveStyle(spec);
          AttributedString styledStr = new AttributedString(spec, style);
          builder.append(String.format("%-25s", spec));
          builder.append(" ");
          builder.append(styledStr);
          builder.append("\n");
        });
    return builder.toAttributedString();
  }

  @Command(command = "theme style list", description = "List styles")
  public AttributedString styleList() {
    AttributedStringBuilder builder = new AttributedStringBuilder();
    themeTags.stream()
        .forEach(tag -> {
          String resolvedStyle = themeResolver.resolveStyleTag(tag);
          AttributedStyle style = themeResolver.resolveStyle(resolvedStyle);
          AttributedString styledStr = new AttributedString(tag, style);
          builder.append(String.format("%-25s", tag));
          builder.append(" ");
          builder.append(styledStr);
          builder.append("\n");
        });
    return builder.toAttributedString();
  }

  @Command(command = "theme style resolve", description = "Resolve given style")
  public AttributedString styleResolve(
      @Option(longNames = "--spec", defaultValue = "default") String spec
  ) {
    AttributedStringBuilder builder = new AttributedStringBuilder();
    AttributedStyle style = themeResolver.resolveStyle(spec);
    AttributedString styledStr = new AttributedString(spec, style);
    builder.append(styledStr);
    builder.append("\n");
    return builder.toAttributedString();
  }

  @Command(command = "theme expression resolve", description = "Resolve given style expression")
  public AttributedString expressionResolve(
      @Option(longNames = "--expression", defaultValue = "hi @{bold from} expression") String expression
  ) {
    AttributedStringBuilder builder = new AttributedStringBuilder();
    AttributedString styledStr = themeResolver.evaluateExpression(expression);
    builder.append(styledStr);
    builder.append("\n");
    return builder.toAttributedString();
  }

  @Command(command = "theme figure list", description = "List figures")
  public AttributedString figureList() {
    AttributedStringBuilder builder = new AttributedStringBuilder();
    Stream.of(FigureSettings.tags())
        .forEach(tag -> {
          builder.append(String.format("%-25s", tag));
          builder.append(" ");
          String resolveFigureTag = themeResolver.resolveFigureTag(tag);
          combinations3().stream().forEach(spec -> {
            AttributedStyle style = themeResolver.resolveStyle(spec);
            builder.append(" ");
            builder.append(new AttributedString(resolveFigureTag, style));
          });
          builder.append("\n");
        });
    return builder.toAttributedString();
  }

  private List<String> combinations1() {
    List<String> styles = new ArrayList<>();
    colorGround.stream().forEach(ground -> {
      colors.stream().forEach(color -> {
        named.stream().forEach(named -> {
          styles.add(String.format("%s,%s:%s", named, ground, color));
        });
      });
    });
    return styles;
  }

  private List<String> combinations2() {
    List<String> styles = new ArrayList<>();
    rgbRedHue.stream().forEach(rgb -> {
      styles.add(String.format("inverse,fg-rgb:%s", rgb));
    });
    return styles;
  }

  private List<String> combinations3() {
    List<String> styles = new ArrayList<>();
    Arrays.asList("fg").stream().forEach(ground -> {
      Arrays.asList("white").stream().forEach(color -> {
        named.stream().forEach(named -> {
          styles.add(String.format("%s,%s:%s", named, ground, color));
        });
      });
    });
    return styles;
  }
}
