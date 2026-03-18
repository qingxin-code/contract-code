package com.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.Result;
import com.cms.entity.Merchant;
import com.cms.security.LoginInterceptor;
import com.cms.service.IMerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

    @Autowired
    private IMerchantService merchantService;

    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestParam(required = false) String name) {
        Long userId = LoginInterceptor.getCurrentId();
        Page<Merchant> page = new Page<>(current, size);
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getUserId, userId)
                .orderByDesc(Merchant::getCreateTime);

        if (StringUtils.hasText(name)) {
            wrapper.and(w -> w.like(Merchant::getMerchantName, name)
                    .or().like(Merchant::getMerchantCode, name)
                    .or().like(Merchant::getContactPerson, name)
                    .or().like(Merchant::getContactPhone, name)
                    .or().like(Merchant::getAddress, name));
        }
        return Result.success(merchantService.page(page, wrapper));
    }

    @PostMapping("/add")
    public Result add(@RequestBody Merchant merchant) {
        merchant.setUserId(LoginInterceptor.getCurrentId());
        merchantService.save(merchant);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Merchant merchant) {
        Merchant old = merchantService.getById(merchant.getId());
        if (old == null || !old.getUserId().equals(LoginInterceptor.getCurrentId())) {
            return Result.error("无权修改");
        }
        merchantService.updateById(merchant);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        Merchant old = merchantService.getById(id);
        if (old == null || !old.getUserId().equals(LoginInterceptor.getCurrentId())) {
            return Result.error("无权删除");
        }
        merchantService.removeById(id);
        return Result.success();
    }
}
