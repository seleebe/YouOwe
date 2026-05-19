package com.seleebe.youowe.expense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class CreateExpenseDto {

  @NotNull(message = "ID плательщика не может быть пустым")
  private Long payerId;

  @NotNull(message = "ID группы не может быть пустым")
  private Long groupId;

  @NotNull(message = "Сумма чека обязательна")
  @Positive(message = "Сумма чека должна быть больше нуля")
  private BigDecimal amount;

  @NotBlank(message = "Описание чека не может быть пустым")
  private String description;

  private SplitType splitType;

  private List<Long> involvedUserIds;

  private Map<Long, BigDecimal> exactSplits;
}
