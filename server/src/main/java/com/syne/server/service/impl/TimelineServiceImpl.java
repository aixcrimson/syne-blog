package com.syne.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syne.server.common.Result;
import com.syne.server.entity.Timeline;
import com.syne.server.exception.BusinessException;
import com.syne.server.mapper.TimelineMapper;
import com.syne.server.service.TimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 时间线管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimelineServiceImpl extends ServiceImpl<TimelineMapper, Timeline> implements TimelineService {

    private final TimelineMapper timelineMapper;

    @Override
    public List<Timeline> getAllTimelines() {
        LambdaQueryWrapper<Timeline> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Timeline::getDeleted, 0)
                .orderByDesc(Timeline::getYear)
                .orderByDesc(Timeline::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public Timeline getTimelineById(Long id) {
        Timeline timeline = super.getById(id);
        if (timeline == null || timeline.getDeleted() == 1) {
            throw new BusinessException("时间线不存在");
        }
        return timeline;
    }

    @Override
    public Timeline createTimeline(Timeline timeline) {
        // 设置默认值

        if (timeline.getColor() == null) {
            timeline.setColor("primary");
        }
        
        this.save(timeline);
        log.info("创建时间线成功: id={}, title={}", timeline.getId(), timeline.getTitle());
        return timeline;
    }

    @Override
    public Timeline updateTimeline(Long id, Timeline timeline) {
        Timeline existing = this.getTimelineById(id);
        
        existing.setYear(timeline.getYear());
        existing.setTitle(timeline.getTitle());
        existing.setDescription(timeline.getDescription());
        existing.setIcon(timeline.getIcon());
        existing.setColor(timeline.getColor());

        
        this.updateById(existing);
        log.info("更新时间线成功: id={}", id);
        return this.getTimelineById(id);
    }

    @Override
    @Transactional
    public Result<String> deleteTimelines(String ids) {
        if (!StringUtils.hasText(ids)) {
            throw new BusinessException("时间线ID不能为空");
        }

        String[] idArray = ids.split(",");
        List<String> failedIds = new ArrayList<>();
        int successCount = 0;

        for (String idStr : idArray) {
            try {
                Long id = Long.parseLong(idStr.trim());
                this.deleteTimeline(id);
                successCount++;
            } catch (NumberFormatException e) {
                failedIds.add(idStr + "(格式错误)");
            } catch (BusinessException e) {
                failedIds.add(idStr + "(" + e.getMessage() + ")");
            }
        }

        String message;
        if (failedIds.isEmpty()) {
            message = successCount == 1 ? "删除时间线成功" :
                    String.format("成功删除 %d 个时间线", successCount);
        } else {
            message = String.format("成功删除 %d 个时间线，失败 %s",
                    successCount, String.join(", ", failedIds));
        }

        return Result.success(message);
    }

    /**
     * 逻辑删除单个时间线
     */
    private void deleteTimeline(Long id) {
        Timeline timeline = this.getTimelineById(id);
        
        LambdaUpdateWrapper<Timeline> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Timeline::getId, id)
                .set(Timeline::getDeleted, 1)
                .set(Timeline::getUpdateTime, LocalDateTime.now());
        
        this.update(updateWrapper);
        log.info("删除时间线成功: id={}", id);
    }
}
