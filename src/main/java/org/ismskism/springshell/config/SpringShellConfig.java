package org.ismskism.springshell.config;

import org.ismskism.springshell.utils.*;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class SpringShellConfig {

  @Bean
  public ComponentHelper componentHelper() {
    return new ComponentHelper();
  }
  @Bean
  public ShellHelper shellHelper(@Lazy Terminal terminal) {
    return new ShellHelper(terminal);
  }
  @Bean
  public InputReader inputReader(@Lazy LineReader lineReader, ShellHelper shellHelper) {
    return new InputReader(lineReader, shellHelper);
  }
  @Bean
  public ProgressCounter progressCounter(@Lazy Terminal terminal) {
    return new ProgressCounter(terminal);
  }
  @Bean
  public ProgressBar progressBar(ShellHelper shellHelper) {
    return new ProgressBar(shellHelper);
  }
  @Bean
  public StaticProgressBar staticProgressBar(ShellHelper shellHelper) {
    return new StaticProgressBar(shellHelper);
  }

}
