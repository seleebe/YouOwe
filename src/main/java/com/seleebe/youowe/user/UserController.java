package com.seleebe.youowe.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/{id}")
  public UserResponseDto getUser(@PathVariable Long id) {
    return userService.getUserById(id);
  }

  @GetMapping
  public List<UserResponseDto> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/me/dashboard")
  public DashboardResponseDto getMyDashboard(
      @AuthenticationPrincipal UserDetails currentUser
  ) {
    return userService.getUserDashboard(currentUser.getUsername());
  }

  @DeleteMapping("/me")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteMe(
      @AuthenticationPrincipal UserDetails currentUser
  ) {
    userService.deleteCurrentUser(currentUser.getUsername());
  }
}
