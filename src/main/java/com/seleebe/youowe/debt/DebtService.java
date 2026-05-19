package com.seleebe.youowe.debt;

import com.seleebe.youowe.exception.InvalidBusinessOperationException;
import com.seleebe.youowe.exception.ResourceNotFoundException;
import com.seleebe.youowe.group.Group;
import com.seleebe.youowe.group.GroupService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import com.seleebe.youowe.user.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebtService {

  private final DebtRepository debtRepository;
  private final GroupService groupService;

  @Transactional
  public void updateDebt(Group group, User debtor, User creditor, BigDecimal amount) {
    if (debtor.getId().equals(creditor.getId())) {
      return;
    }

    Optional<Debt> existingDebtOpt = debtRepository.findByGroupAndDebtorAndCreditor(group, debtor,
        creditor);

    Optional<Debt> reverseDebtOpt = debtRepository.findByGroupAndDebtorAndCreditor(group, creditor,
        debtor);

    if (reverseDebtOpt.isPresent()) {
      Debt reverseDebt = reverseDebtOpt.get();
      int comparison = reverseDebt.getAmount().compareTo(amount);

      if (comparison > 0) {
        reverseDebt.setAmount(reverseDebt.getAmount().subtract(amount));
        debtRepository.save(reverseDebt);
      } else if (comparison < 0) {
        debtRepository.delete(reverseDebt);
        Debt newDebt = new Debt();
        newDebt.setGroup(group);
        newDebt.setAmount(amount.subtract(reverseDebt.getAmount()));
        newDebt.setCreditor(creditor);
        newDebt.setDebtor(debtor);
        debtRepository.save(newDebt);
      }
    } else {
      if (existingDebtOpt.isPresent()) {
        Debt existingDebt = existingDebtOpt.get();
        existingDebt.setAmount(existingDebt.getAmount().add(amount));
        debtRepository.save(existingDebt);
      } else {
        Debt newDebt = new Debt();
        newDebt.setAmount(amount);
        newDebt.setGroup(group);
        newDebt.setCreditor(creditor);
        newDebt.setDebtor(debtor);
        debtRepository.save(newDebt);
      }
    }
  }

  public List<DebtResponseDto> getGroupDebts(Long groupId) {
    Group group = groupService.getGroupEntity(groupId);
    List<Debt> debts = debtRepository.findByGroup(group);

    return debts.stream()
        .map(this::mapToResponseDto)
        .toList();
  }

  private DebtResponseDto mapToResponseDto(Debt debt) {
    return DebtResponseDto.builder()
        .id(debt.getId())
        .debtorId(debt.getDebtor().getId())
        .debtorName(debt.getDebtor().getUsername())
        .creditorId(debt.getCreditor().getId())
        .creditorName(debt.getCreditor().getUsername())
        .amount(debt.getAmount())
        .build();
  }

  @Transactional
  public void settleDebt(Long debtId, Long creditorId) {
    Debt debt = debtRepository.findById(debtId)
        .orElseThrow(() -> new ResourceNotFoundException("Debt not found with id: " + debtId));

    if (!debt.getCreditor().getId().equals(creditorId)) {
      throw new InvalidBusinessOperationException(
          "Only the creditor can mark this debt as settled");
    }

    debtRepository.delete(debt);
  }
}
