package com.seleebe.youowe.user;

import com.seleebe.youowe.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public User getUserEntity(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
  }

  @Transactional
  public UserResponseDto createUser(CreateUserDto dto) {
    User user = new User();
    user.setUsername(dto.getUsername());
    user.setPassword(dto.getPassword());
    user.setPhoneNumber(dto.getPhoneNumber());

    User savedUser = userRepository.save(user);
    return mapToResponseDto(savedUser);
  }

  public UserResponseDto getUserById(Long id) {
    User user = getUserEntity(id);
    return mapToResponseDto(user);
  }

  public List<UserResponseDto> getAllUsers() {
    return userRepository.findAll().stream()
        .map(this::mapToResponseDto)
        .toList();
  }

  private UserResponseDto mapToResponseDto(User user) {
    return UserResponseDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .phoneNumber(user.getPhoneNumber())
        .build();
  }
}
