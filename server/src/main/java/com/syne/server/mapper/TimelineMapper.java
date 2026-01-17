package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.model.entity.Timeline;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TimelineMapper extends BaseMapper<Timeline> {

    /**
     * 查询时间线
     * @return 列表
     */
    @Select("SELECT * FROM timelines WHERE deleted = 0 ORDER BY sort_order DESC")
    List<Timeline> selectAllTimelines();
}
