package com.syne.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.syne.server.entity.User;
import com.syne.server.entity.dto.LoginDTO;
import com.syne.server.entity.vo.LoginVO;
import com.syne.server.entity.vo.UserInfoVO;

/**
 * 认证服务接口
 */
public interface AuthService extends IService<User> {

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录响应
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);

    /**
     * 验证密码
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean matchesPassword(String rawPassword, String encodedPassword);

    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息
     */
    UserInfoVO getCurrentUser();
}
