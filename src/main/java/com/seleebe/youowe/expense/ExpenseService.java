package com.seleebe.youowe.expense;

import com.seleebe.youowe.debt.DebtService;
import com.seleebe.youowe.exception.InvalidBusinessOperationException;
import com.seleebe.youowe.exception.ResourceNotFoundException;
import com.seleebe.youowe.group.Group;
import com.seleebe.youowe.user.User;
import com.seleebe.youowe.group.GroupRepository;
import com.seleebe.youowe.user.UserRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final DebtService debtService;

  @Transactional
  public ExpenseResponseDto createExpense(CreateExpenseDto dto) {
    User payer = userRepository.findById(dto.getPayerId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Group group = groupRepository.findById(dto.getGroupId())
        .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

    if (!group.getMembers().contains(payer)) {
      throw new InvalidBusinessOperationException("Group does not contain payer");
    }

    Expense expense = new Expense();
    expense.setAmount(dto.getAmount());
    expense.setGroup(group);
    expense.setDescription(dto.getDescription());
    expense.setPayer(payer);
    Expense savedExpense = expenseRepository.save(expense);

    switch (dto.getSplitType()) {
      case EQUAL -> processEqualSplit(group, payer, dto.getAmount(), dto.getInvolvedUserIds());
      case EXACT -> processExactSplit(group, payer, dto.getAmount(), dto.getExactSplits());
      default -> throw new InvalidBusinessOperationException("Unsupported split type");
    }

    return mapToResponseDto(savedExpense);
  }

  private void processEqualSplit(Group group, User payer, BigDecimal amount,
      List<Long> involvedUserIds) {
    if (involvedUserIds == null || involvedUserIds.isEmpty()) {
      throw new InvalidBusinessOperationException(
          "Involved users list cannot be empty for EQUAL split");
    }

    List<User> involvedUsers = group.getMembers().stream()
        .filter(u -> involvedUserIds.contains(u.getId()))
        .toList();

    if (involvedUsers.size() != involvedUserIds.size()) {
      throw new InvalidBusinessOperationException("Some involved users are not part of the group");
    }

    BigDecimal splitAmount = amount.divide(BigDecimal.valueOf(involvedUsers.size()), 2,
        RoundingMode.HALF_UP);

    for (User user : involvedUsers) {
      if (!user.getId().equals(payer.getId())) {
        debtService.updateDebt(group, user, payer, splitAmount);
      }
    }
  }

  private void processExactSplit(Group group, User payer, BigDecimal amount,
      Map<Long, BigDecimal> exactSplits) {
    if (exactSplits == null || exactSplits.isEmpty()) {
      throw new InvalidBusinessOperationException("Splits map cannot be empty");
    }

    BigDecimal totalSplitSum = exactSplits.values().stream()
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (totalSplitSum.compareTo(amount) != 0) {
      throw new InvalidBusinessOperationException(
          "Sum of exact splits does not match the total expense amount");
    }

    List<User> involvedUsers = group.getMembers().stream()
        .filter(u -> exactSplits.containsKey(u.getId()))
        .toList();

    if (involvedUsers.size() != exactSplits.size()) {
      throw new InvalidBusinessOperationException(
          "Some users in the split are not part of the group");
    }

    for (User user : involvedUsers) {
      if (!user.getId().equals(payer.getId())) {
        BigDecimal userShare = exactSplits.get(user.getId());
        debtService.updateDebt(group, user, payer, userShare);
      }
    }
  }

  private ExpenseResponseDto mapToResponseDto(Expense expense) {
    return ExpenseResponseDto.builder()
        .id(expense.getId())
        .payerName(expense.getPayer().getUsername())
        .payerId(expense.getPayer().getId())
        .groupId(expense.getGroup().getId())
        .amount(expense.getAmount())
        .description(expense.getDescription())
        .createdAt(expense.getCreatedAt())
        .build();
  }
}