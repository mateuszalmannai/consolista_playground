package org.ismskism.springshell.service.demo;

import org.ismskism.springshell.model.demo.CliUser;

import java.util.List;

public interface UserService {
  CliUser findById(Long id);
  CliUser findByUsername(String username);
  List<CliUser> findAll();
  boolean exists(String username);
  CliUser create(CliUser user);
  CliUser update(CliUser user);
  long updateAll();
  CliUser getLoggedInUser();
}
