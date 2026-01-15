package com.syne.server.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 批量排序DTO
 */
@Data
@Schema(description = "批量排序DTO")
public class SortOrderDTO {

    @Schema(description = "排序项列表")
    @NotEmpty(message = "排序项列表不能为空")
    @Valid
    private List<SortOrderItem> orders;

    /**
     * 单个排序项
     */
    @Data
    @Schema(description = "排序项")
    public static class SortOrderItem {
        
        @Schema(description = "ID")
        @NotNull(message = "ID不能为空")
        private Long id;

        @Schema(description = "排序值")
        @NotNull(message = "排序值不能为空")
        private Integer sortOrder;

        @Schema(description = "分类ID（站点跨分类拖拽时使用）")
        private Long categoryId;
    }
}
