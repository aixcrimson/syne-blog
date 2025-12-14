package com.syne.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.entity.User;
import com.syne.server.entity.dto.UserDTO;
import com.syne.server.entity.vo.UserListVO;
import com.syne.server.exception.BusinessException;
import com.syne.server.mapper.UserMapper;
import com.syne.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<UserListVO> getUserList(PageQuery pageQuery, Integer role, Integer status,
                                              String keyword, LocalDateTime startTime, LocalDateTime endTime) {
        // 查询用户列表
        List<UserListVO> list = userMapper.selectUserList(
            role,
            status,
            keyword,
            startTime,
            endTime,
            pageQuery.getOffset(),
            pageQuery.getPageSize()
        );

        // 查询总数
        Long total = userMapper.countUsers(role, status, keyword, startTime, endTime);

        // 构建分页结果返回
        return PageResult.build(
            pageQuery.getPage(),
            pageQuery.getPageSize(),
            total,
            list
        );
    }

    @Override
    public User getUserById(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        return user;
    }

    @Override
    @Transactional
    public User createUser(UserDTO userCreateDTO) {
        // 验证用户名是否已存在
        Integer usernameCount = userMapper.checkUsernameExists(userCreateDTO.getUsername(), null);
        if (usernameCount > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 验证邮箱是否已存在
        Integer emailCount = userMapper.checkEmailExists(userCreateDTO.getEmail(), null);
        if (emailCount > 0) {
            throw new BusinessException("邮箱已存在");
        }

        // 创建用户实体
        User user = new User();
        BeanUtils.copyProperties(userCreateDTO, user);

        // 设置默认值
        if (user.getRole() == null) {
            user.setRole(2); // 默认为普通用户
        }
        if (user.getStatus() == null) {
            user.setStatus(1); // 默认为正常状态
        }

        // 加密密码
        String encodedPassword = passwordEncoder.encode(userCreateDTO.getPassword());
        user.setPasswordHash(encodedPassword);

        // 保存用户
        this.save(user);

        log.info("创建用户：{}", user.getUsername());
        return user;
    }

    @Override
    @Transactional
    public User updateUser(UserDTO userUpdateDTO, Long id) {
        // 获取用户
        User user = this.getById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }

        // 验证邮箱是否与其他用户重复
        if (StringUtils.hasText(userUpdateDTO.getEmail()) &&
            !user.getEmail().equals(userUpdateDTO.getEmail())) {
            Integer emailCount = userMapper.checkEmailExists(userUpdateDTO.getEmail(), id);
            if (emailCount > 0) {
                throw new BusinessException("邮箱已存在");
            }
        }

        // 更新用户信息
        BeanUtils.copyProperties(userUpdateDTO, user, "id", "username", "passwordHash");
        user.setId(id);

        // 更新用户
        this.updateById(user);

        log.info("更新用户：id={}, {}", id, user.getUsername());
        return this.getById(user.getId());
    }

    @Override
    @Transactional
    public Result<String> deleteUsers(String ids) {
        log.info("删除用户：ids={}", ids);
        if (!StringUtils.hasText(ids)) {
            throw new BusinessException("用户ID不能为空");
        }

        String[] idArray = ids.split(",");
        if (idArray.length > 100) {
            throw new BusinessException("单次删除数量不能超过100个");
        }

        List<String> failedIds = new ArrayList<>();
        int successCount = 0;

        for (String idStr : idArray) {
            try {
                Long id = Long.parseLong(idStr.trim());
                this.deleteUser(id);
                successCount++;
            } catch (NumberFormatException e) {
                failedIds.add(idStr + "(格式错误)");
                log.warn("无效的用户ID格式：{}", idStr);
            } catch (BusinessException e) {
                failedIds.add(idStr + "(" + e.getMessage() + ")");
                log.warn("删除用户失败：id={}, 原因={}", idStr, e.getMessage());
            }
        }

        // 构建返回消息
        String message;
        if (failedIds.isEmpty()) {
            message = successCount == 1 ? "用户删除成功" : "成功删除 " + successCount + " 个用户";
            return Result.success(message);
        } else {
            message = "成功删除 " + successCount + " 个用户，失败：" + String.join(", ", failedIds);
            return Result.success(message);
        }
    }

    /**
     * 删除单个用户
     */
    private void deleteUser(Long id) {
        User user = this.getById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户是否有关联数据
        String relatedData = userMapper.checkUserRelatedData(id);
        if (relatedData != null) {
            String[] parts = relatedData.split(",");
            int articleCount = 0;
            int commentCount = 0;

            for (String part : parts) {
                if (part.startsWith("文章:")) {
                    articleCount = Integer.parseInt(part.substring(3));
                } else if (part.startsWith("评论:")) {
                    commentCount = Integer.parseInt(part.substring(3));
                }
            }

            if (articleCount > 0 || commentCount > 0) {
                throw new BusinessException(
                    String.format("用户'%s'有%d篇文章和%d条评论，建议禁用而不是删除",
                    user.getUsername(), articleCount, commentCount));
            }
        }

        // 逻辑删除
        this.removeById(id);
    }
}