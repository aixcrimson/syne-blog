package com.syne.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 月度计数VO
 * 用于文章发布趋势等按月聚合的数据
 */
@Data
@Schema(description = "月度计数")
public class MonthlyCountVO {

    @Schema(description = "月份，格式: yyyy-MM")
    private String month;

    @Schema(description = "数量")
    private Long count;
}
