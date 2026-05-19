package com.seleebe.youowe.expense;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpenseResponseDto {

  private Long id;
  private Long payerId;
  private String payerName;
  private Long groupId;
  private BigDecimal amount;
  private String description;
  private LocalDateTime createdAt;
}
