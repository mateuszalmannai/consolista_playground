package org.ismskism.springshell.commands.newapi.demo;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.component.*;
import org.springframework.shell.component.support.Itemable;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@ShellComponent
@Command(group = "Components")
public class ComponentCommands extends AbstractShellComponent {
  @Command(command = "component confirmation", description = "Confirmation input")
  public String confirmationInput(boolean no) {
    ConfirmationInput component = new ConfirmationInput(getTerminal(), "Enter value", !no);
    component.setResourceLoader(getResourceLoader());
    component.setTemplateExecutor(getTemplateExecutor());
    ConfirmationInput.ConfirmationInputContext context = component.run(ConfirmationInput.ConfirmationInputContext.empty());
    return "Got value " + context.getResultValue();
  }

  @Command(command = "component single", description = "Single selector")
  public String singleSelector() {
    SelectorItem<String> i1 = SelectorItem.of("key1", "value1");
    SelectorItem<String> i2 = SelectorItem.of("key2", "value2");
    List<SelectorItem<String>> items = Arrays.asList(i1, i2);
    SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
        items, "testSimple", null);
    component.setResourceLoader(getResourceLoader());
    component.setTemplateExecutor(getTemplateExecutor());
    SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
        .run(SingleItemSelector.SingleItemSelectorContext.empty());
    String result = context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
    return "Got value " + result;
  }

  @Command(command = "component multi", description = "Multi selector")
  public String multiSelector() {
    List<SelectorItem<String>> items = new ArrayList<>();
    items.add(SelectorItem.of("key1", "value1"));
    items.add(SelectorItem.of("key2", "value2", false, true));
    items.add(SelectorItem.of("key3", "value3"));
    MultiItemSelector<String, SelectorItem<String>> component = new MultiItemSelector<>(getTerminal(),
        items, "testSimple", null);
    component.setResourceLoader(getResourceLoader());
    component.setTemplateExecutor(getTemplateExecutor());
    MultiItemSelector.MultiItemSelectorContext<String, SelectorItem<String>> context = component
        .run(MultiItemSelector.MultiItemSelectorContext.empty());
    String result = context.getResultItems().stream()
        .map(Itemable::getItem)
        .collect(Collectors.joining(","));
    return "Got value " + result;
  }

  @Command(command = "component path", description = "Path input")
  public String pathInput() {
    PathInput component = new PathInput(getTerminal(), "Enter value");
    component.setResourceLoader(getResourceLoader());
    component.setTemplateExecutor(getTemplateExecutor());
    PathInput.PathInputContext context = component.run(PathInput.PathInputContext.empty());
    return "Got value " + context.getResultValue();
  }

  @Command(command = "component path search", description = "Path search")
  public String pathSearch(
      @Option(defaultValue = ShellOption.NULL) Integer maxPathsShow,
      @Option(defaultValue = ShellOption.NULL) Integer maxPathsSearch,
      @Option(defaultValue = "true") boolean searchForward,
      @Option(defaultValue = "false") boolean searchCaseSensitive,
      @Option(defaultValue = "false") boolean searchNormalize
  ) {
    PathSearch.PathSearchConfig config = new PathSearch.PathSearchConfig();
    if (maxPathsShow != null) {
      config.setMaxPathsShow(maxPathsShow);
    }
    if (maxPathsSearch != null) {
      config.setMaxPathsSearch(maxPathsSearch);
    }
    config.setSearchForward(searchForward);
    config.setSearchCaseSensitive(searchCaseSensitive);
    config.setSearchNormalize(searchNormalize);
    PathSearch component = new PathSearch(getTerminal(), "Enter value", config);
    component.setResourceLoader(getResourceLoader());
    component.setTemplateExecutor(getTemplateExecutor());
    PathSearch.PathSearchContext context = component.run(PathSearch.PathSearchContext.empty());
    return "Got value " + context.getResultValue();
  }

  @Command(command = "component string", description = "String input")
  public String stringInput(boolean mask) {
    StringInput component = new StringInput(getTerminal(), "Enter value", "myvalue");
    component.setResourceLoader(getResourceLoader());
    component.setTemplateExecutor(getTemplateExecutor());
    if (mask) {
      component.setMaskCharacter('*');
    }
    StringInput.StringInputContext context = component.run(StringInput.StringInputContext.empty());
    return "Got value " + context.getResultValue();
  }

  @Command(command = "component string custom", description = "String input")
  public String stringInputCustom(boolean mask) {
    StringInput component = new StringInput(getTerminal(), "Enter value", "myvalue",
        new StringInputCustomRenderer());
    component.setResourceLoader(getResourceLoader());
    component.setTemplateExecutor(getTemplateExecutor());
    if (mask) {
      component.setMaskCharacter('*');
    }
    StringInput.StringInputContext context = component.run(StringInput.StringInputContext.empty());
    return "Got value " + context.getResultValue();
  }

  private static class StringInputCustomRenderer implements Function<StringInput.StringInputContext, List<AttributedString>> {

    @Override
    public List<AttributedString> apply(StringInput.StringInputContext context) {
      AttributedStringBuilder builder = new AttributedStringBuilder();
      builder.append(context.getName());
      builder.append(" ");

      if (context.getResultValue() != null) {
        builder.append(context.getResultValue());
      }
      else  {
        String input = context.getInput();
        if (StringUtils.hasText(input)) {
          builder.append(input);
        }
        else {
          builder.append("[Default " + context.getDefaultValue() + "]");
        }
      }

      return Arrays.asList(builder.toAttributedString());
    }
  }
}
