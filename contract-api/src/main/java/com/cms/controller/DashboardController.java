package com.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cms.common.Result;
import com.cms.dto.DashboardSummaryDTO;
import com.cms.entity.Contract;
import com.cms.entity.Merchant;
import com.cms.security.LoginInterceptor;
import com.cms.service.IContractService;
import com.cms.service.IMerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private static final List<String> PENDING_STATUSES = Arrays.asList("待签署", "待审批");

    @Autowired
    private IContractService contractService;

    @Autowired
    private IMerchantService merchantService;

    @GetMapping("/summary")
    public Result<DashboardSummaryDTO> getSummary() {
        Long userId = LoginInterceptor.getCurrentId();
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.with(TemporalAdjusters.lastDayOfMonth());

        DashboardSummaryDTO dto = new DashboardSummaryDTO();
        dto.setContractTotal(contractService.lambdaQuery()
                .eq(Contract::getUserId, userId)
                .count());
        dto.setPendingCount(contractService.lambdaQuery()
                .eq(Contract::getUserId, userId)
                .in(Contract::getStatus, PENDING_STATUSES)
                .count());
        dto.setMerchantTotal(merchantService.lambdaQuery()
                .eq(Merchant::getUserId, userId)
                .count());
        dto.setMonthlyAmount(queryMonthlyAmount(userId, monthStart, monthEnd));
        dto.setExpiringCount(contractService.lambdaQuery()
                .eq(Contract::getUserId, userId)
                .isNotNull(Contract::getExpireDate)
                .ge(Contract::getExpireDate, today)
                .le(Contract::getExpireDate, today.plusDays(30))
                .notIn(Contract::getStatus, Arrays.asList("已归档", "已完成"))
                .count());

        return Result.success(dto);
    }

    private BigDecimal queryMonthlyAmount(Long userId, LocalDate monthStart, LocalDate monthEnd) {
        QueryWrapper<Contract> wrapper = new QueryWrapper<>();
        wrapper.select("COALESCE(SUM(amount), 0) AS totalAmount")
                .eq("user_id", userId)
                .between("sign_date", monthStart, monthEnd);

        Map<String, Object> result = contractService.getMap(wrapper);
        if (result == null || result.get("totalAmount") == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(result.get("totalAmount").toString());
    }
}
