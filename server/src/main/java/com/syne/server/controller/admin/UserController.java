package com.syne.server.controller.admin;

import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.model.entity.User;
import com.syne.server.model.dto.UserDTO;
import com.syne.server.model.dto.UserUpdateDTO;
import com.syne.server.model.dto.ChangePasswordDTO;
import com.syne.server.model.vo.UserListVO;
import com.syne.server.service.UserService;
import com.syne.server.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 管理员用户控制器
 */
@Slf4j
@Tag(name = "管理员用户管理", description = "管理员用户相关接口")
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户列表
     *
     * @param page      页码
     * @param pageSize  每页大小
     * @param role      用户角色
     * @param status    账号状态
     * @param keyword   搜索关键词
     * @param startTime 注册开始时间
     * @param endTime   注册结束时间
     * @return 用户分页列表
     */
    @Operation(summary = "查询用户列表", description = "管理员分页查询用户列表，支持按角色、状态、关键词筛选")
    @GetMapping
    public Result<PageResult<UserListVO>> getUserList(
        @Parameter(description = "页码", example = "1")
        @RequestParam(defaultValue = "1") Integer page,

        @Parameter(description = "每页大小", example = "10")
        @RequestParam(defaultValue = "10") Integer pageSize,

        @Parameter(description = "用户角色（1-管理员，2-普通用户）", example = "1")
        @RequestParam(required = false) Integer role,

        @Parameter(description = "账号状态（0-禁用，1-正常）", example = "1")
        @RequestParam(required = false) Integer status,

        @Parameter(description = "搜索关键词", example = "Syne")
        @RequestParam(required = false) String keyword,

        @Parameter(description = "注册开始时间", example = "2025-12-01 00:00:00")
        @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,

        @Parameter(description = "注册结束时间", example = "2025-12-31 23:59:59")
        @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime
    ) {
        log.info("分页查询用户列表：page={}, pageSize={}, role={}, status={}, keyword={}, startTime={}, endTime={}",
                page, pageSize, role, status, keyword, startTime, endTime);

        PageQuery pageQuery = new PageQuery();
        pageQuery.setPage(page);
        pageQuery.setPageSize(pageSize);

        PageResult<UserListVO> result = userService.getUserList(
            pageQuery, role, status, keyword, startTime, endTime);

        return Result.success(result);
    }

    /**
     * 根据ID获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @Operation(summary = "获取用户详情", description = "管理员根据ID获取用户详情")
    @GetMapping("/{id}")
    public Result<User> getUserById(
        @Parameter(description = "用户ID", required = true)
        @PathVariable Long id
    ) {
        log.info("获取用户详情：id={}", id);
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    /**
     * 创建用户
     *
     * @param userDTO 用户创建数据
     * @return 创建的用户
     */
    @Operation(summary = "创建用户", description = "管理员创建新用户")
    @PostMapping
    public Result<User> createUser(
        @Parameter(description = "用户创建数据", required = true)
        @Valid @RequestBody UserDTO userDTO
    ) {
        log.info("创建用户：{}", userDTO.getUsername());
        User createdUser = userService.createUser(userDTO);
        return Result.success(createdUser);
    }

    /**
     * 更新用户信息
     *
     * @param userDTO 用户更新数据
     * @param id            用户ID
     * @return 更新结果
     */
    @Operation(summary = "更新用户信息", description = "管理员更新用户信息")
    @PutMapping("/{id}")
    public Result<User> updateUser(
        @Parameter(description = "用户更新数据", required = true)
        @Valid @RequestBody UserUpdateDTO userUpdateDTO,

        @Parameter(description = "用户ID", required = true)
        @PathVariable Long id
    ) {
        log.info("更新用户信息：id={}, userUpdateDTO={}", id, userUpdateDTO);
        User updatedUser = userService.updateUser(userUpdateDTO, id);
        return Result.success(updatedUser);
    }

    /**
     * 修改密码
     *
     * @param changePasswordDTO 修改密码参数
     * @return 结果
     */
    @Operation(summary = "修改密码", description = "用户修改自己的登录密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(
        @Parameter(description = "修改密码参数", required = true)
        @Valid @RequestBody ChangePasswordDTO changePasswordDTO
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("修改密码：userId={}", userId);
        userService.updatePassword(changePasswordDTO, userId);
        return Result.success();
    }

    /**
     * 删除用户
     *
     * @param ids 用户ID字符串
     * @return 删除结果
     */
    @Operation(summary = "删除用户", description = "删除单个或多个用户（逻辑删除），支持批量操作")
    @DeleteMapping
    public Result<String> deleteUsers(
        @Parameter(description = "用户ID，单个或多个用英文逗号分隔", required = true, example = "1,2,3")
        @RequestParam String ids
    ) {
        log.info("删除用户：ids={}", ids);
        Result<String> result = userService.deleteUsers(ids);
        return result;
    }

}