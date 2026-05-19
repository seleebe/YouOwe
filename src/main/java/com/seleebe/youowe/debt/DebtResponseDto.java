package com.seleebe.youowe.debt;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DebtResponseDto {

  private Long id;
  private Long debtorId;
  private String debtorName;
  private Long creditorId;
  private String creditorName;
  private BigDecimal amount;
}
