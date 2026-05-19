package com.seleebe.youowe.debt;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debts")
@RequiredArgsConstructor
public class DebtController {

  private final DebtService debtService;

  @GetMapping("/group/{groupId}")
  public List<DebtResponseDto> getGroupDebts(@PathVariable Long groupId) {
    return debtService.getGroupDebts(groupId);
  }

  @PostMapping("/{debtId}/settle")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void settleDebt(
      @PathVariable Long debtId,
      @RequestParam Long creditorId) {

    debtService.settleDebt(debtId, creditorId);
  }
}
