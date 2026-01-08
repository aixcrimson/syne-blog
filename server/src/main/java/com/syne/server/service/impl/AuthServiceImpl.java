package com.syne.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syne.server.common.Result;
import com.syne.server.entity.User;
import com.syne.server.entity.dto.LoginDTO;
import com.syne.server.entity.dto.RegisterDTO;
import com.syne.server.entity.vo.LoginVO;
import com.syne.server.entity.vo.UserInfoVO;
import com.syne.server.exception.BusinessException;
import com.syne.server.mapper.AuthMapper;
import com.syne.server.service.AuthService;
import com.syne.server.utils.JwtUtil;
import com.syne.server.utils.SecurityUtils;
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
        LoginVO loginVO = new LoginVO();

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

        // 4. 验证密码
        if (!matchesPassword(loginDTO.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(400, "用户名或密码错误");
        }

        // 5. 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 6.构造返回VO
        loginVO.setToken(token);
        loginVO.setId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setEmail(user.getEmail());
        loginVO.setRole(user.getRole());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setBio(user.getBio());
        loginVO.setGithub(user.getGithub());
        loginVO.setBilibili(user.getBilibili());
        loginVO.setExpiresIn(jwtExpiration);

        // 7. 返回登录响应
        return loginVO;
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

    @Override
    public UserInfoVO getCurrentUser() {
        UserInfoVO userInfoVO = new UserInfoVO();

        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "用户未登录");
        }

        // 获取当前用户信息
        User user = getById(userId);
        if (user == null || user.getDeleted() != 0) {
            throw new BusinessException(404, "用户不存在");
        }

        userInfoVO.setId(user.getId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setEmail(user.getEmail());
        userInfoVO.setAvatar(user.getAvatar());
        userInfoVO.setBio(user.getBio());
        userInfoVO.setGithub(user.getGithub());
        userInfoVO.setBilibili(user.getBilibili());
        userInfoVO.setRole(user.getRole());

        return userInfoVO;
    }

    @Override
    public Result<String> register(RegisterDTO registerDTO){
        // 1.检查用户名是否存在
        User byUsername = getByUsername(registerDTO.getUsername());
        if(byUsername != null){
            throw new BusinessException(400, "用户名已存在");
        }

        // 2.检查邮箱是否存在
        LambdaQueryWrapper<User> queryWrapper  = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, registerDTO.getEmail())
                .eq(User::getDeleted, 0);
        User byEmail = getOne(queryWrapper);
        if(byEmail != null){
            throw new BusinessException(400, "邮箱已存在");
        }

        // 3.构建新用户
        User newUser = new User();
        newUser.setUsername(registerDTO.getUsername());
        newUser.setEmail(registerDTO.getEmail());
        // 密码加密
        newUser.setPasswordHash(passwordEncoder.encode(registerDTO.getPassword()));
        // 默认头像
        newUser.setAvatar("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");
        // 默认角色：普通用户
        newUser.setRole(2);
        // 默认状态：正常
        newUser.setStatus(1);
        newUser.setBio("这个人很懒，什么都没有写~");

        // 4.保存用户
        boolean save = save(newUser);
        if(!save){
            throw new BusinessException(500, "注册失败，请稍后重试");
        }

        return Result.success("注册成功");
    }
}
