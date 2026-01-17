package com.syne.server.controller.web;

import com.syne.server.common.Result;
import com.syne.server.model.dto.LoginDTO;
import com.syne.server.model.dto.RegisterDTO;
import com.syne.server.model.vo.LoginVO;
import com.syne.server.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.syne.server.model.vo.UserInfoVO;

/**
 * 认证控制器
 */
@Slf4j
@Tag(name = "用户端认证接口", description = "用户端认证相关接口")
@RestController("webAuthController")
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    /**
     * 获取当前登录用户信息
     * @return 当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "根据JWT Token获取当前登录用户信息")
    @GetMapping("/me")
    public Result<UserInfoVO> getCurrentUser() {
        log.info("获取当前用户信息");
        UserInfoVO userInfo = authService.getCurrentUser();
        return Result.success(userInfo);
    }

    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 注册响应
     */
    @Operation(summary = "用户注册", description = "用户注册")
    @PostMapping("/register")
    public Result<String> register(
            @Parameter(description = "注册信息", required = true)
            @Valid @RequestBody RegisterDTO registerDTO
    ) {
        log.info("用户注册: {}", registerDTO.getUsername());
        return authService.register(registerDTO);
    }

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 登录响应
     */
    @Operation(summary = "用户登录", description = "用户名密码登录，返回JWT Token")
    @PostMapping("/login")
    public Result<LoginVO> login(
            @Parameter(description = "登录信息", required = true)
            @Valid @RequestBody LoginDTO loginDTO
    ) {
        log.info("用户登录: {}", loginDTO.getUsername());
        LoginVO loginVO = authService.login(loginDTO);
        return Result.success("登录成功", loginVO);
    }
}