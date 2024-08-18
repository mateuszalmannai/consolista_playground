package org.ismskism.springshell.commands.newapi.demo;

import org.ismskism.springshell.utils.InputReader;
import org.ismskism.springshell.utils.ShellHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.util.StringUtils;

@Command(group = "User Command")
public class AuthenticationCommands {

  @Lazy
  @Autowired
  ShellHelper shellHelper;

  @Lazy
  @Autowired
  InputReader inputReader;

  @Autowired
  AuthenticationManager authenticationManager;

  @Command(command = "sign-in", description = "Sign in as a known user")
  public void signin() {
    String username;
    boolean usernameInvalid = true;
    do {
      username = inputReader.prompt("Please enter your username");
      if (StringUtils.hasText(username)) {
        usernameInvalid = false;
      } else {
        shellHelper.printWarning("Username can not be empty string!");
      }
    } while (usernameInvalid);
    String password = inputReader.prompt("Please enter your password", null, false);
    Authentication request = new UsernamePasswordAuthenticationToken(username, password);

    try {
      Authentication result = authenticationManager.authenticate(request);
      SecurityContextHolder.getContext().setAuthentication(result);
      shellHelper.printSuccess("Credentials successfully authenticated! " + username + " -> welcome to CliDemo.");
    } catch (AuthenticationException e) {
      shellHelper.printWarning("Authentication failed: " + e.getMessage());
    }
  }

  @Command(command = "sign-out", description = "Sign authenticated user out.")
  @CommandAvailability(provider = {"isUserLoggedIn"})
  public void signout() {
    SecurityContextHolder.clearContext();
    shellHelper.printSuccess("Successfully signed out.");
  }
}
