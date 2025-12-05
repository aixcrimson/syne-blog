package com.syne.server.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一响应状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "没有权限访问"),
    NOT_FOUND(404, "请求的资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    REQUEST_TIMEOUT(408, "请求超时"),
    CONFLICT(409, "数据冲突"),
    UNSUPPORTED_MEDIA_TYPE(415, "不支持的媒体类型"),

    // 服务器错误 5xx
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    NOT_IMPLEMENTED(501, "功能未实现"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // 业务错误 1xxx
    VALIDATION_ERROR(1001, "参数校验失败"),
    DUPLICATE_DATA(1002, "数据已存在"),
    DATA_NOT_FOUND(1003, "数据不存在"),
    DATA_ERROR(1004, "数据异常"),

    // 用户相关错误 2xxx
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_DISABLED(2002, "用户已被禁用"),
    USERNAME_EXISTS(2003, "用户名已存在"),
    EMAIL_EXISTS(2004, "邮箱已存在"),
    PASSWORD_ERROR(2005, "密码错误"),

    // 文章相关错误 3xxx
    ARTICLE_NOT_FOUND(3001, "文章不存在"),
    ARTICLE_ALREADY_PUBLISHED(3002, "文章已发布"),
    ARTICLE_NOT_PUBLISHED(3003, "文章未发布"),

    // 评论相关错误 4xxx
    COMMENT_NOT_FOUND(4001, "评论不存在"),
    COMMENT_DISABLED(4002, "评论已被禁用");

    private final Integer code;
    private final String message;
}
