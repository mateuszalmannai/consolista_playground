package org.ismskism.springshell.utils;

import org.springframework.shell.component.ConfirmationInput;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ComponentHelper extends AbstractShellComponent {

  public boolean getConditional(String prompt, boolean defaultValue) {
    ConfirmationInput component = new ConfirmationInput(getTerminal(), prompt, defaultValue);
    component.setResourceLoader(getResourceLoader());
    component.setTemplateExecutor(getTemplateExecutor());
    ConfirmationInput.ConfirmationInputContext context = component.run(ConfirmationInput.ConfirmationInputContext.empty());
    return context.getResultValue();
  }

  public String stringInput(String prompt, String defaultValue, boolean mask) {
    StringInput component = new StringInput(getTerminal(), prompt, defaultValue);
    component.setResourceLoader(getResourceLoader());
    component.setTemplateExecutor(getTemplateExecutor());
    if (mask) {
      component.setMaskCharacter('*');
    }
    StringInput.StringInputContext context = component.run(StringInput.StringInputContext.empty());
    return context.getResultValue();
  }

  public String singleSelector(String prompt, Map<String, String> keyValuePairs) {
    List<SelectorItem<String>> items = keyValuePairs.entrySet()
        .stream()
        .map(entry -> SelectorItem.of(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
        items, prompt, null);
    component.setResourceLoader(getResourceLoader());
    component.setTemplateExecutor(getTemplateExecutor());
    SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
        .run(SingleItemSelector.SingleItemSelectorContext.empty());
    return context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
  }
}
