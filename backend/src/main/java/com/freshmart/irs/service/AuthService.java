package com.freshmart.irs.service;

import com.freshmart.irs.dto.auth.LoginRequest;
import com.freshmart.irs.dto.auth.LoginResponse;
import com.freshmart.irs.dto.auth.MeResponse;

import java.util.List;

/**
 * 认证服务：处理登录、注册、登出与当前用户信息查询，并记录审计事件。
 */
public interface AuthService {
    /**
     * 登录校验并签发 JWT。
     *
     * @param request 登录请求
     * @return 登录响应（包含 token 与 roles）
     */
    LoginResponse login(LoginRequest request);

    /**
     * 顾客注册：创建用户并默认赋予 CUSTOMER 角色。
     *
     * @param username 用户名（唯一）
     * @param password 明文密码
     * @param phone    手机号（可空）
     * @param email    邮箱（可空）
     * @return 用户 ID
     */
    long register(String username, String password, String phone, String email);

    /**
     * 获取当前登录用户信息：每次仅查库校验 status，其他信息从 Token/上下文获取。
     *
     * @return 当前用户信息
     */
    MeResponse me();

    /**
     * 获取当前登录用户角色列表（来自 Token/上下文）。
     *
     * @return 角色编码列表
     */
    List<String> permissions();

    /**
     * 登出：服务端不做 token 黑名单，仅记录审计。
     */
    void logout();
}
