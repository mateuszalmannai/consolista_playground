package org.ismskism.springshell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;


@SpringBootApplication
@CommandScan
public class SpringshellApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringshellApplication.class, args);
  }

}
