package com.cms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("contract")
public class Contract {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "合同编号不能为空")
    private String contractNo;

    @NotBlank(message = "合同名称不能为空")
    private String contractName;

    @NotBlank(message = "合同类型不能为空")
    private String contractType;

    @NotBlank(message = "相对方不能为空")
    private String counterparty;

    @NotNull(message = "合同金额不能为空")
    @DecimalMin(value = "0.01", message = "合同金额必须大于 0")
    private BigDecimal amount;

    @NotNull(message = "签订日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate signDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate expireDate;

    private String status;
    private String attachment;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
