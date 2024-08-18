package org.ismskism.springshell.service.demo;

import org.ismskism.springshell.model.demo.CliUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CliDemoUserDetailsService implements UserDetailsService {

  private final UserService userService;

  public CliDemoUserDetailsService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    CliUser cliUser = userService.findByUsername(username);

    if (cliUser == null) {
      throw new UsernameNotFoundException("User not found.");
    }

    User.UserBuilder builder = User.withUsername(username);
    builder.password(new BCryptPasswordEncoder().encode(cliUser.getPassword()));
    if (cliUser.isSuperuser()) {
      builder.roles("USER", "ADMIN");
    } else {
      builder.roles("USER");
    }
    return builder.build();
  }
}
