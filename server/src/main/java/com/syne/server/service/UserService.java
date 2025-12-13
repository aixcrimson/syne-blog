package com.syne.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.syne.server.common.PageQuery;
import com.syne.server.common.PageResult;
import com.syne.server.common.Result;
import com.syne.server.entity.User;
import com.syne.server.entity.dto.UserDTO;
import com.syne.server.entity.vo.UserListVO;

import java.time.LocalDateTime;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 分页查询用户列表
     *
     * @param pageQuery  分页查询参数
     * @param role       用户角色
     * @param status     账号状态
     * @param keyword    搜索关键词
     * @param startTime  注册开始时间
     * @param endTime    注册结束时间
     * @return 用户分页列表
     */
    PageResult<UserListVO> getUserList(PageQuery pageQuery, Integer role, Integer status,
                                       String keyword, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据ID获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    User getUserById(Long id);

    /**
     * 创建用户
     *
     * @param userCreateDTO 用户创建数据
     * @return 创建的用户
     */
    User createUser(UserDTO userCreateDTO);

    /**
     * 更新用户信息
     *
     * @param userUpdateDTO 用户更新数据
     * @param id            用户ID
     * @return 更新后的用户
     */
    User updateUser(UserDTO userUpdateDTO, Long id);

    /**
     * 删除用户
     *
     * @param ids 用户ID字符串
     * @return 删除结果
     */
    Result<String> deleteUsers(String ids);
}