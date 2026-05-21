package com.seleebe.youowe.group;

import com.seleebe.youowe.debt.DebtRepository;
import com.seleebe.youowe.exception.ActiveDebtException;
import com.seleebe.youowe.exception.NoAccessException;
import com.seleebe.youowe.exception.ResourceNotFoundException;
import com.seleebe.youowe.user.User;
import com.seleebe.youowe.user.UserRepository;
import com.seleebe.youowe.user.UserResponseDto;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

  private final GroupRepository groupRepository;

  private final UserRepository userRepository;

  private final DebtRepository debtRepository;

  public Group getGroupEntity(Long id) {
    return groupRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));
  }

  @Transactional
  public GroupResponseDto createGroup(CreateGroupDto dto, String currentUsername) {
    User creator = userRepository.findByUsername(currentUsername)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Creator not found with username: " + currentUsername));

    Group group = new Group();
    group.setName(dto.getName());
    group.getMembers().add(creator);

    Group savedGroup = groupRepository.save(group);

    return mapToResponseDto(savedGroup);
  }

  public GroupResponseDto getGroupById(Long id) {
    Group group = getGroupEntity(id);
    return mapToResponseDto(group);
  }

  public boolean hasActiveDebts(Group group, User user) {
    return debtRepository.existsByGroupAndDebtor(group, user)
        || debtRepository.existsByGroupAndCreditor(group, user);
  }

  @Transactional
  public GroupResponseDto addUserToGroup(Long groupId, Long userId, String currentUsername) {
    Group group = getGroupEntity(groupId);

    if (!group.getAdmin().getUsername().equals(currentUsername)) {
      throw new NoAccessException("Only group creator can delete the group");
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    group.getMembers().add(user);
    Group savedGroup = groupRepository.save(group);

    return mapToResponseDto(savedGroup);
  }

  private GroupResponseDto mapToResponseDto(Group group) {
    List<UserResponseDto> members = group.getMembers().stream()
        .map(user -> UserResponseDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .build())
        .toList();

    return GroupResponseDto.builder()
        .id(group.getId())
        .name(group.getName())
        .members(members)
        .build();
  }

  @Transactional
  public void removeUserFromGroup(Long groupId, Long userId, String currentUsername) {
    Group group = getGroupEntity(groupId);

    if (!group.getAdmin().getUsername().equals(currentUsername)) {
      throw new NoAccessException("Only group creator can delete the group");
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    if (hasActiveDebts(group, user)) {
      throw new ActiveDebtException("Cannot leave group with active debts");
    }
    group.getMembers().remove(user);
    groupRepository.save(group);
  }

  @Transactional
  public void deleteGroup(Long groupId, String currentUsername) {
    Group group = getGroupEntity(groupId);

    if (!group.getAdmin().getUsername().equals(currentUsername)) {
      throw new NoAccessException("Only group creator can delete the group");
    }

    if (debtRepository.existsByGroup(group)) {
      throw new ActiveDebtException("Can not delete the group until all debts are settled");
    }

    groupRepository.delete(group);
  }
}
