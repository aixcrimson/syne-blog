package com.syne.server.controller.admin;

import com.syne.server.common.Result;
import com.syne.server.model.dto.LoginDTO;
import com.syne.server.model.vo.LoginVO;
import com.syne.server.model.vo.UserInfoVO;
import com.syne.server.exception.BusinessException;
import com.syne.server.service.AuthService;
import com.syne.server.security.TokenBlacklistService;
import com.syne.server.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 认证控制器
 */
@Slf4j
@Tag(name = "认证管理", description = "认证相关接口")
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * 用户登录
     *
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
        if(loginVO.getRole() != 1){
            throw new BusinessException(403, "普通用户禁止登录管理后台");
        }
        return Result.success("登录成功", loginVO);
    }

    /**
     * 退出登录
     *
     * @param token JWT Token
     * @return 退出登录响应
     */
    @Operation(summary = "退出登录", description = "用户退出登录")
    @PostMapping("/logout")
    public Result<String> logout(
            @Parameter(description = "JWT Token", hidden = true)
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        if (!StringUtils.hasText(token)) {
            log.info("用户退出登录（未携带Token）");
            return Result.success("退出登录成功");
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Date expiration = jwtUtil.getExpirationFromToken(token);
        if (expiration != null) {
            tokenBlacklistService.blacklist(token, expiration);
        } else {
            log.warn("退出登录时解析Token过期时间失败");
        }

        log.info("用户退出登录");
        return Result.success("退出登录成功");
    }

    /**
     * 刷新Token
     *
     * @param token 旧的JWT Token
     * @return 新的JWT Token
     */
    @Operation(summary = "刷新Token", description = "刷新过期的JWT Token")
    @PostMapping("/refresh")
    public Result<String> refreshToken(
            @Parameter(description = "JWT Token", hidden = true)
            @RequestHeader(value = "Authorization") String token
    ) {
        try {
            // 移除Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 验证旧Token
            if (jwtUtil.isTokenExpired(token)) {
                return Result.error(401, "Token已过期且无法刷新");
            }

            // 刷新Token
            String newToken = jwtUtil.refreshToken(token);
            if (newToken == null) {
                return Result.error(400, "Token刷新失败");
            }

            return Result.success("Token刷新成功", newToken);
        } catch (Exception e) {
            log.error("Token刷新失败", e);
            return Result.error(400, "Token刷新失败");
        }
    }

    /**
     * 获取当前用户
     *
     * @return 当前用户信息
     */
    @Operation(summary = "获取当前用户", description = "获取当前用户信息")
    @GetMapping("/current")
    public Result<UserInfoVO> getCurrentUser() {
        UserInfoVO userInfoVO = authService.getCurrentUser();
        return Result.success("获取当前用户成功", userInfoVO);
    }
}
