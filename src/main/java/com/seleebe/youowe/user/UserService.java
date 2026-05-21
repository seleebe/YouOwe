package com.seleebe.youowe.user;

import com.seleebe.youowe.debt.DebtRepository;
import com.seleebe.youowe.debt.DebtSummaryDto;
import com.seleebe.youowe.exception.ActiveDebtException;
import com.seleebe.youowe.exception.NoAccessException;
import com.seleebe.youowe.exception.ResourceNotFoundException;
import com.seleebe.youowe.group.GroupRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final DebtRepository debtRepository;
  private final GroupRepository groupRepository;

  public User getUserEntity(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
  }

  @Transactional
  public UserResponseDto createUser(CreateUserDto dto) {
    User user = new User();
    user.setUsername(dto.getUsername());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    user.setPaymentInfo(dto.getPaymentInfo());

    User savedUser = userRepository.save(user);
    return mapToResponseDto(savedUser);
  }

  @Transactional(readOnly = true)
  public DashboardResponseDto getUserDashboard(String currentUsername) {
    User currentUser = userRepository.findByUsername(currentUsername)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    List<DebtSummaryDto> iOwe = debtRepository.findAllByDebtor(currentUser).stream()
        .map(debt -> new DebtSummaryDto(
            debt.getGroup().getName(),
            debt.getCreditor().getUsername(),
            debt.getCreditor().getPaymentInfo(),
            debt.getAmount()
        )).toList();

    List<DebtSummaryDto> owesMe = debtRepository.findAllByCreditor(currentUser).stream()
        .map(debt -> new DebtSummaryDto(
            debt.getGroup().getName(),
            debt.getDebtor().getUsername(),
            null,
            debt.getAmount()
        )).toList();

    return new DashboardResponseDto(iOwe, owesMe);
  }

  @Transactional
  public void deleteCurrentUser(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(
            () -> new ResourceNotFoundException("User not found with username:" + username));

    if (groupRepository.existsByAdmin(user)) {
      throw new NoAccessException(
          "Can't delete your account while you are admin of the group(s). Please delete the group(s).");
    }

    if (debtRepository.existsByDebtor(user) || debtRepository.existsByCreditor(user)) {
      throw new ActiveDebtException(
          "Can't delete your account while you have unclosed financial agreements");
    }
    userRepository.delete(user);
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
        .paymentInfo(user.getPaymentInfo())
        .build();
  }
}
