package com.cms.controller;

import com.cms.common.Result;
import com.cms.entity.SysUser;
import com.cms.service.ISysUserService;
import com.cms.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private ISysUserService sysUserService;

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }
        
        SysUser user = sysUserService.login(username, password);
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("nickname", user.getNickname());
        data.put("username", user.getUsername());
        return Result.success(data);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody SysUser user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return Result.error("用户名和密码不能为空");
        }
        sysUserService.register(user);
        return Result.success("注册成功");
    }
}
