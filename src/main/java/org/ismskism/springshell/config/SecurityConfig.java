package org.ismskism.springshell.config;

import org.ismskism.springshell.service.demo.CliDemoUserDetailsService;
import org.ismskism.springshell.service.demo.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.shell.Availability;
import org.springframework.shell.AvailabilityProvider;

import java.util.List;

@Configuration
public class SecurityConfig {
  @Bean
  public UserDetailsService userDetailsService(UserService userService) {
    return new CliDemoUserDetailsService(userService);
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  };

  @Bean
  public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(userDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder);

    return new ProviderManager(List.of(authenticationProvider));
  }

  @Bean
  public AvailabilityProvider isUserLoggedIn() {
    return () -> {
      if (checkIfUserIsLoggedIn()) {
        return Availability.available();
      } else {
        return Availability.unavailable("you are not signed in. Please sign in to use the command.");
      }
    };
  }

  @Bean
  public AvailabilityProvider isUserAdmin() {
    return () -> {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null || !(authentication instanceof  UsernamePasswordAuthenticationToken)) {
        return Availability.unavailable("you are not signedIn. Please sign in to be able to use this command!");
      }
      boolean hasAdminRole = authentication.getAuthorities().stream()
          .anyMatch(grantedAuthority -> grantedAuthority.equals(new SimpleGrantedAuthority("ROLE_ADMIN")));
      if (!hasAdminRole) {
        return Availability.unavailable("you have insufficient privileges to run this command!");
      }
      return Availability.available();
    };
  }

  private boolean checkIfUserIsLoggedIn() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && authentication instanceof UsernamePasswordAuthenticationToken;
  }

}
