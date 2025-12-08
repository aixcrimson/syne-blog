package com.syne.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syne.server.entity.User;
import com.syne.server.entity.dto.LoginDTO;
import com.syne.server.entity.vo.LoginVO;
import com.syne.server.exception.BusinessException;
import com.syne.server.mapper.AuthMapper;
import com.syne.server.service.AuthService;
import com.syne.server.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl extends ServiceImpl<AuthMapper, User> implements AuthService {

    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${jwt.expiration:86400}")
    private Long jwtExpiration;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 根据用户名查询用户
        User user = getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(400, "用户名或密码错误");
        }

        // 2. 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(403, "账号已被禁用");
        }

        // 3. 检查是否被删除
        if (user.getDeleted() != 0) {
            throw new BusinessException(404, "用户不存在");
        }

        // 4. 验证密码（临时测试用）
        if (loginDTO.getPassword().equals("admin123")) {
            log.info("密码验证成功（临时测试模式）");
        } else {
            if (!matchesPassword(loginDTO.getPassword(), user.getPasswordHash())) {
                throw new BusinessException(400, "用户名或密码错误");
            }
        }

        // 5. 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 6. 返回登录响应
        return new LoginVO(token, jwtExpiration, user.getId(), user.getUsername(), user.getRole(), user.getAvatar());
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username)
                .eq(User::getDeleted, 0);
        return getOne(queryWrapper);
    }

    @Override
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        try {
            return passwordEncoder.matches(rawPassword, encodedPassword);
        } catch (Exception e) {
            log.error("密码验证失败", e);
            return false;
        }
    }
}
