package org.ismskism.springshell.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ismskism.springshell.service.demo.MockUserService;
import org.ismskism.springshell.service.demo.ProgressUpdateObserver;
import org.ismskism.springshell.service.demo.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class UserServiceConfig {

  @Bean
  public UserService userService(ProgressUpdateObserver observer, ObjectMapper objectMapper) throws IOException {
    MockUserService userService = new MockUserService();
    userService.setObserver(observer);
    userService.setObjectMapper(objectMapper);
    userService.init("cli-users.json");
    return userService;
  }

}
