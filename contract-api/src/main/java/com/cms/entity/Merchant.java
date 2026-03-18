package com.cms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("merchant")
public class Merchant {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "客商名称不能为空")
    private String merchantName;

    @NotBlank(message = "客商编号不能为空")
    private String merchantCode;

    private String contactPerson;
    private String contactPhone;
    private String address;
    private String status;
    private Long userId;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
