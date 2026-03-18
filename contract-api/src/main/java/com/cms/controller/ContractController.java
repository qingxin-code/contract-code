package com.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.CustomException;
import com.cms.common.Result;
import com.cms.entity.Contract;
import com.cms.security.LoginInterceptor;
import com.cms.service.IContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contracts")
public class ContractController {
    private static final String DEFAULT_STATUS = "待签署";

    @Autowired
    private IContractService contractService;

    @GetMapping
    public Result<Page<Contract>> getContracts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String searchKey,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String contractType) {
        Long userId = LoginInterceptor.getCurrentId();
        Page<Contract> contractPage = new Page<>(page, size);
        QueryWrapper<Contract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        if (StringUtils.hasText(contractType)) {
            queryWrapper.eq("contract_type", contractType);
        }
        if (StringUtils.hasText(searchKey)) {
            queryWrapper.and(wrapper -> wrapper.like("contract_name", searchKey)
                    .or().like("contract_no", searchKey)
                    .or().like("counterparty", searchKey));
        }

        queryWrapper.orderByDesc("updated_at").orderByDesc("id");
        return Result.success(contractService.page(contractPage, queryWrapper));
    }

    @GetMapping("/{id}")
    public Result<Contract> getContract(@PathVariable Long id) {
        Contract contract = contractService.getById(id);
        if (contract == null) {
            return Result.error("合同不存在");
        }
        if (!contract.getUserId().equals(LoginInterceptor.getCurrentId())) {
            return Result.error(403, "无权访问该合同");
        }
        return Result.success(contract);
    }

    @PostMapping
    public Result<String> createContract(@Validated @RequestBody Contract contract) {
        Long currentUserId = LoginInterceptor.getCurrentId();
        contract.setUserId(currentUserId);
        normalizeContract(contract);
        validateUniqueContractNo(currentUserId, contract.getContractNo(), null);
        contractService.save(contract);
        return Result.success("合同创建成功");
    }

    @PutMapping("/{id}")
    public Result<String> updateContract(@PathVariable Long id, @Validated @RequestBody Contract contract) {
        Long currentUserId = LoginInterceptor.getCurrentId();
        Contract existing = contractService.getById(id);
        if (existing == null) {
            return Result.error("合同不存在");
        }
        if (!existing.getUserId().equals(currentUserId)) {
            return Result.error(403, "无权修改该合同");
        }

        contract.setId(id);
        contract.setUserId(existing.getUserId());
        normalizeContract(contract);
        validateUniqueContractNo(currentUserId, contract.getContractNo(), id);
        contractService.updateById(contract);
        return Result.success("合同更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteContract(@PathVariable Long id) {
        Contract existing = contractService.getById(id);
        if (existing == null) {
            return Result.error("合同不存在");
        }
        if (!existing.getUserId().equals(LoginInterceptor.getCurrentId())) {
            return Result.error(403, "无权删除该合同");
        }
        contractService.removeById(id);
        return Result.success("合同删除成功");
    }

    @PostMapping("/batchDelete")
    public Result<String> batchDeleteContracts(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("删除列表不能为空");
        }

        Long currentUserId = LoginInterceptor.getCurrentId();
        long ownedCount = contractService.lambdaQuery()
                .in(Contract::getId, ids)
                .eq(Contract::getUserId, currentUserId)
                .count();
        if (ownedCount != ids.size()) {
            return Result.error(403, "批量删除中包含无权限合同");
        }

        contractService.removeByIds(ids);
        return Result.success("批量删除成功");
    }

    private void normalizeContract(Contract contract) {
        if (StringUtils.hasText(contract.getContractNo())) {
            contract.setContractNo(contract.getContractNo().trim());
        }
        if (StringUtils.hasText(contract.getContractName())) {
            contract.setContractName(contract.getContractName().trim());
        }
        if (StringUtils.hasText(contract.getContractType())) {
            contract.setContractType(contract.getContractType().trim());
        }
        if (StringUtils.hasText(contract.getCounterparty())) {
            contract.setCounterparty(contract.getCounterparty().trim());
        }
        if (!StringUtils.hasText(contract.getStatus())) {
            contract.setStatus(DEFAULT_STATUS);
        }
        if (!StringUtils.hasText(contract.getAttachment())) {
            contract.setAttachment(null);
        } else {
            contract.setAttachment(contract.getAttachment().trim());
        }
    }

    private void validateUniqueContractNo(Long userId, String contractNo, Long currentId) {
        if (!StringUtils.hasText(contractNo) || userId == null) {
            return;
        }

        QueryWrapper<Contract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("contract_no", contractNo.trim());
        if (currentId != null) {
            queryWrapper.ne("id", currentId);
        }

        if (contractService.count(queryWrapper) > 0) {
            throw new CustomException(400, "你已创建过相同合同编号，请更换后再试");
        }
    }
}
