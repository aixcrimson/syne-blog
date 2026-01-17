package com.syne.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.syne.server.common.Result;
import com.syne.server.model.entity.Timeline;

import java.util.List;

/**
 * 时间线管理服务接口
 */
public interface TimelineService extends IService<Timeline> {

    /**
     * 获取所有时间线列表
     * @return 时间线列表
     */
    List<Timeline> getAllTimelines();

    /**
     * 根据ID获取时间线
     * @param id 时间线ID
     * @return 时间线信息
     */
    Timeline getTimelineById(Long id);

    /**
     * 创建时间线
     * @param timeline 时间线数据
     * @return 创建的时间线
     */
    Timeline createTimeline(Timeline timeline);

    /**
     * 更新时间线
     * @param id 时间线ID
     * @param timeline 时间线数据
     * @return 更新后的时间线
     */
    Timeline updateTimeline(Long id, Timeline timeline);

    /**
     * 删除时间线
     * @param ids 时间线ID，多个用逗号分隔
     * @return 删除结果
     */
    Result<String> deleteTimelines(String ids);
}
