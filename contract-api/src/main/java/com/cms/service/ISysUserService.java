package com.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cms.entity.SysUser;

public interface ISysUserService extends IService<SysUser> {
    SysUser login(String username, String password);
    void register(SysUser user);
}
