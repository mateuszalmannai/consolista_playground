package org.ismskism.springshell.commands.newapi.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.ResultMode;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.component.support.Nameable;
import org.springframework.shell.standard.ShellOption;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Command(group = "Flow")
public class ComponentFlowCommands {

  @Autowired
  private ComponentFlow.Builder componentFlowBuilder;

  @Command(command = "flow showcase1", description = "Showcase")
  public void showcase1() {
    Map<String, String> single1SelectItems = new HashMap<>();
    single1SelectItems.put("key1", "value1");
    single1SelectItems.put("key2", "value2");
    List<SelectItem> multi1SelectItems = Arrays.asList(SelectItem.of("key1", "value1"),
        SelectItem.of("key2", "value2"), SelectItem.of("key3", "value3"));
    ComponentFlow flow = componentFlowBuilder.clone()
        .reset()
        .withStringInput("field1")
        .name("Field1")
        .defaultValue("defaultField1Value")
        .and()
        .withStringInput("field2")
        .name("Field2")
        .and()
        .withConfirmationInput("confirmation1")
        .name("Confirmation1")
        .and()
        .withPathInput("path1")
        .name("Path1")
        .and()
        .withSingleItemSelector("single1")
        .name("Single1")
        .selectItems(single1SelectItems)
        .and()
        .withMultiItemSelector("multi1")
        .name("Multi1")
        .selectItems(multi1SelectItems)
        .and()
        .build();
    flow.run();
  }

  @Command(command = "flow showcase2", description = "Showcase")
  public String showcase2(
      @Option(description = "Field1 value", defaultValue = ShellOption.NULL) String field1,
      @Option(description = "Field2 value", defaultValue = ShellOption.NULL) String field2,
      @Option(description = "Confirmation1 value", defaultValue = ShellOption.NULL) Boolean confirmation1,
      @Option(description = "Path1 value", defaultValue = ShellOption.NULL) String path1,
      @Option(description = "Single1 value", defaultValue = ShellOption.NULL) String single1,
      @Option(description = "Multi1 value", defaultValue = ShellOption.NULL) List<String> multi1
  ) {
    Map<String, String> single1SelectItems = new HashMap<>();
    single1SelectItems.put("key1", "value1");
    single1SelectItems.put("key2", "value2");
    List<SelectItem> multi1SelectItems = Arrays.asList(SelectItem.of("key1", "value1"),
        SelectItem.of("key2", "value2"), SelectItem.of("key3", "value3"));
    List<String> multi1ResultValues = multi1 != null ? multi1 : new ArrayList<>();
    ComponentFlow flow = componentFlowBuilder.clone().reset()
        .withStringInput("field1")
        .name("Field1")
        .defaultValue("defaultField1Value")
        .resultValue(field1)
        .resultMode(ResultMode.ACCEPT)
        .and()
        .withStringInput("field2")
        .name("Field2")
        .resultValue(field2)
        .resultMode(ResultMode.ACCEPT)
        .and()
        .withConfirmationInput("confirmation1")
        .name("Confirmation1")
        .resultValue(confirmation1)
        .resultMode(ResultMode.ACCEPT)
        .and()
        .withPathInput("path1")
        .name("Path1")
        .resultValue(path1)
        .resultMode(ResultMode.ACCEPT)
        .and()
        .withSingleItemSelector("single1")
        .name("Single1")
        .selectItems(single1SelectItems)
        .resultValue(single1)
        .resultMode(ResultMode.ACCEPT)
        .and()
        .withMultiItemSelector("multi1")
        .name("Multi1")
        .selectItems(multi1SelectItems)
        .resultValues(multi1ResultValues)
        .resultMode(ResultMode.ACCEPT)
        .and()
        .build();
    ComponentFlow.ComponentFlowResult result = flow.run();
    StringBuilder buf = new StringBuilder();
    result.getContext().stream().forEach(e -> {
      buf.append(e.getKey());
      buf.append(" = ");
      buf.append(e.getValue());
      buf.append("\n");
    });
    return buf.toString();
  }

  @Command(command = "flow conditional", description = "Second component based on first")
  public void conditional() {
    Map<String, String> single1SelectItems = new HashMap<>();
    single1SelectItems.put("Field1", "field1");
    single1SelectItems.put("Field2", "field2");
    ComponentFlow flow = componentFlowBuilder.clone().reset()
        .withSingleItemSelector("single1")
        .name("Single1")
        .selectItems(single1SelectItems)
        .next(ctx -> ctx.getResultItem().get().getItem())
        .and()
        .withStringInput("field1")
        .name("Field1")
        .defaultValue("defaultField1Value")
        .next(ctx -> null)
        .and()
        .withStringInput("field2")
        .name("Field2")
        .defaultValue("defaultField2Value")
        .next(ctx -> null)
        .and()
        .build();
    flow.run();
  }

  @Command(command = "flow autoselect", description = "Autoselect item")
  public void autoselect(
      @ShellOption(defaultValue = "Field3") String defaultValue
  ) {
    Map<String, String> single1SelectItems = IntStream.range(1, 10)
        .boxed()
        .collect(Collectors.toMap(i -> "Field" + i, i -> "field" + i));

    ComponentFlow flow = componentFlowBuilder.clone().reset()
        .withSingleItemSelector("single1")
        .name("Single1")
        .selectItems(single1SelectItems)
        .defaultSelect(defaultValue)
        .sort(Comparator.comparing(Nameable::getName))
        .and()
        .build();
    flow.run();
  }
}
