package com.syne.server.controller.web;

import com.syne.server.common.Result;
import com.syne.server.model.entity.User;
import com.syne.server.model.dto.ChangePasswordDTO;
import com.syne.server.model.dto.UserUpdateDTO;
import com.syne.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.syne.server.exception.BusinessException;
import com.syne.server.utils.SecurityUtils;

/**
 * 网站用户控制器
 */
@Slf4j
@Tag(name = "用户管理", description = "用户相关接口")
@RestController("webUserController")
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    /**
     * 更新用户信息
     *
     * @param userUpdateDTO 用户更新数据
     * @param id            用户ID
     * @return 更新结果
     */
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的个人信息")
    @PutMapping("/{id}")
    public Result<User> updateUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id,

            @Parameter(description = "用户更新数据", required = true)
            @Valid @RequestBody UserUpdateDTO userUpdateDTO
    ) {
        log.info("更新用户信息：id={}, userDTO={}", id, userUpdateDTO);
        
        // 校验当前登录用户是否是操作对象
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!id.equals(currentUserId)) {
            throw new BusinessException(403, "无权修改其他用户的资料");
        }
        
        User updatedUser = userService.updateUser(userUpdateDTO, id);
        return Result.success(updatedUser);
    }

    /**
     * 修改密码
     *
     * @param changePasswordDTO 修改密码参数
     * @param id                用户ID
     * @return 操作结果
     */
    @Operation(summary = "修改密码", description = "修改用户登录密码")
    @PutMapping("/{id}/password")
    public Result<String> updatePassword(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id,

            @Parameter(description = "修改密码参数", required = true)
            @Valid @RequestBody ChangePasswordDTO changePasswordDTO
    ) {
        log.info("修改用户密码：id={}", id);
        
        // 校验当前登录用户是否是操作对象
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!id.equals(currentUserId)) {
            throw new BusinessException(403, "无权修改其他用户的密码");
        }
        
        userService.updatePassword(changePasswordDTO, id);
        return Result.success("密码修改成功");
    }

}