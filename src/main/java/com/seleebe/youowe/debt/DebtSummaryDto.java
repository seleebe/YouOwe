package com.seleebe.youowe.debt;

import java.math.BigDecimal;

public record DebtSummaryDto(
    String GroupName,
    String CounterpartName,
    String paymentInfo,
    BigDecimal amount) {

}
