package com.syne.server.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginVO {

    @Schema(description = "JWT Token")
    private String token;

    @Schema(description = "Token类型", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户角色：1-管理员，2-普通用户")
    private Integer role;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "过期时间（秒）")
    private Long expiresIn;

    public LoginVO(String token, Long expiresIn, Long userId, String username, Integer role, String avatar) {
        this.token = token;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.avatar = avatar;
    }
}