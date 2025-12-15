package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.entity.User;
import com.syne.server.entity.vo.UserListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 查询用户列表
     *
     * @param role      用户角色
     * @param status    账号状态
     * @param keyword   搜索关键词
     * @param startTime 注册开始时间
     * @param endTime   注册结束时间
     * @param offset    分页偏移量
     * @param pageSize  分页大小
     * @return 用户列表
     */
    List<UserListVO> selectUserList(
        @Param("role") Integer role,
        @Param("status") Integer status,
        @Param("keyword") String keyword,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("offset") Integer offset,
        @Param("pageSize") Integer pageSize
    );

    /**
     * 查询用户数量
     *
     * @param role      用户角色
     * @param status    账号状态
     * @param keyword   搜索关键词
     * @param startTime 注册开始时间
     * @param endTime   注册结束时间
     * @return 用户数量
     */
    Long countUsers(
        @Param("role") Integer role,
        @Param("status") Integer status,
        @Param("keyword") String keyword,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    /**
     * 检查用户名是否已存在
     *
     * @param username 用户名
     * @param excludeId 排除的用户ID
     * @return 是否存在
     */
    Integer checkUsernameExists(@Param("username") String username, @Param("excludeId") Long excludeId);

    /**
     * 检查邮箱是否已存在
     *
     * @param email 邮箱
     * @param excludeId 排除的用户ID
     * @return 是否存在
     */
    Integer checkEmailExists(@Param("email") String email, @Param("excludeId") Long excludeId);

    /**
     * 检查用户是否有关联数据
     *
     * @param userId 用户ID
     * @return 关联数据信息
     */
    String checkUserRelatedData(@Param("userId") Long userId);
}