package com.syne.server.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果")
@Builder
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页码")
    private Integer page;

    @Schema(description = "每页大小")
    private Integer pageSize;

     @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "总页数")
    private Integer totalPages;

    @Schema(description = "数据列表")
    private List<T> list;

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> build(Integer page, Integer pageSize, Long total, List<T> list) {
        int totalPages = (int) Math.ceil((double) total / pageSize);
        return new PageResult<>(page, pageSize, total, totalPages, list);
    }
}