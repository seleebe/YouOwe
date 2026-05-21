package com.seleebe.youowe.group;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

  private final GroupService groupService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public GroupResponseDto createGroup(@RequestBody CreateGroupDto dto, @AuthenticationPrincipal
  UserDetails userDetails) {
    return groupService.createGroup(dto, userDetails.getUsername());
  }

  @GetMapping("/{id}")
  public GroupResponseDto getGroup(@PathVariable Long id) {
    return groupService.getGroupById(id);
  }

  @PostMapping("/{groupId}/users/{userId}")
  public GroupResponseDto addUserToGroup(
      @PathVariable Long groupId,
      @PathVariable Long userId,
      @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails currentUser
  ) {
    return groupService.addUserToGroup(groupId, userId, currentUser.getUsername());
  }

  @DeleteMapping("/{groupId}/users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeUserFromGroup(
      @PathVariable Long groupId,
      @PathVariable Long userId,
      @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails currentUser) {
    groupService.removeUserFromGroup(groupId, userId, currentUser.getUsername());
  }

  @DeleteMapping("/{groupId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteGroup(
      @PathVariable Long groupId,
      @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails currentUser
  ) {
    groupService.deleteGroup(groupId, currentUser.getUsername());
  }
}