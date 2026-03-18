package com.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cms.common.CustomException;
import com.cms.entity.SysUser;
import com.cms.mapper.SysUserMapper;
import com.cms.service.ISysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Override
    public SysUser login(String username, String password) {
        SysUser user = this.getOne(new QueryWrapper<SysUser>().eq("username", username));
        if (user == null) {
            throw new CustomException(400, "用户名不存在");
        }
        if (!user.getPassword().equals(password)) {
            // 原型阶段暂使用明文对比，正式环境需加盐 hash
            throw new CustomException(400, "密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new CustomException(403, "账号已被停用");
        }
        return user;
    }

    @Override
    public void register(SysUser user) {
        SysUser existUser = this.getOne(new QueryWrapper<SysUser>().eq("username", user.getUsername()));
        if (existUser != null) {
            throw new CustomException(400, "用户名已存在，请换一个");
        }
        // 原型阶段使用明文存储，设置默认正常状态
        user.setStatus(1);
        this.save(user);
    }
}
