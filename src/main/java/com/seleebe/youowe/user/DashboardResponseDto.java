package com.seleebe.youowe.user;

import com.seleebe.youowe.debt.DebtSummaryDto;
import java.util.List;

public record DashboardResponseDto(
    List<DebtSummaryDto> iOwe,
    List<DebtSummaryDto> owesMe
) {

}
