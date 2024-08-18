package org.ismskism.springshell.commands.programmatic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.command.CommandRegistration;

@Configuration
public class ProgrammaticCommand {

  @Bean
  CommandRegistration commandRegistration() {
    return CommandRegistration.builder()
        .command("programmatic")
        .description("Demonstration of programmatic API")
        .group("Demo")
        .withTarget()
        .function(ctx -> "Testing programmatic commands")
        .and()
        .build();
  }
}
