package com.cms.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardSummaryDTO {
    private Long contractTotal;
    private Long pendingCount;
    private BigDecimal monthlyAmount;
    private Long expiringCount;
    private Long merchantTotal;
}
