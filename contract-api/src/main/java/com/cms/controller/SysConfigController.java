package com.cms.controller;

import com.cms.common.Result;
import com.cms.entity.SysConfig;
import com.cms.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/config")
public class SysConfigController {

    @Autowired
    private ISysConfigService configService;

    @GetMapping("/all")
    public Result all() {
        return Result.success(configService.list());
    }

    @PutMapping("/update")
    public Result update(@RequestBody List<SysConfig> configs) {
        configService.updateBatchById(configs);
        return Result.success();
    }
}
