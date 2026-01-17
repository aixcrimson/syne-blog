package com.syne.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syne.server.common.Result;
import com.syne.server.model.entity.Notice;
import com.syne.server.exception.BusinessException;
import com.syne.server.mapper.NoticeMapper;
import com.syne.server.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 公告管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    private final NoticeMapper noticeMapper;

    @Override
    public List<Notice> getAllNotices() {
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getDeleted, 0)
                .orderByAsc(Notice::getSortOrder)
                .orderByDesc(Notice::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public Notice getNoticeById(Long id) {
        Notice notice = super.getById(id);
        if (notice == null || notice.getDeleted() == 1) {
            throw new BusinessException("公告不存在");
        }
        return notice;
    }

    @Override
    public Notice createNotice(Notice notice) {
        // 设置默认值
        if (notice.getIsShow() == null) {
            notice.setIsShow(1);
        }
        if (notice.getSortOrder() == null) {
            notice.setSortOrder(0);
        }
        
        this.save(notice);
        log.info("创建公告成功: id={}", notice.getId());
        return notice;
    }

    @Override
    public Notice updateNotice(Long id, Notice notice) {
        Notice existing = this.getNoticeById(id);
        
        existing.setContent(notice.getContent());
        existing.setIsShow(notice.getIsShow());
        existing.setSortOrder(notice.getSortOrder());
        
        this.updateById(existing);
        log.info("更新公告成功: id={}", id);
        return this.getNoticeById(id);
    }

    @Override
    @Transactional
    public Result<String> deleteNotices(String ids) {
        if (!StringUtils.hasText(ids)) {
            throw new BusinessException("公告ID不能为空");
        }

        String[] idArray = ids.split(",");
        List<String> failedIds = new ArrayList<>();
        int successCount = 0;

        for (String idStr : idArray) {
            try {
                Long id = Long.parseLong(idStr.trim());
                this.deleteNotice(id);
                successCount++;
            } catch (NumberFormatException e) {
                failedIds.add(idStr + "(格式错误)");
            } catch (BusinessException e) {
                failedIds.add(idStr + "(" + e.getMessage() + ")");
            }
        }

        String message;
        if (failedIds.isEmpty()) {
            message = successCount == 1 ? "删除公告成功" :
                    String.format("成功删除 %d 个公告", successCount);
        } else {
            message = String.format("成功删除 %d 个公告，失败 %s",
                    successCount, String.join(", ", failedIds));
        }

        return Result.success(message);
    }

    /**
     * 逻辑删除单个公告
     */
    private void deleteNotice(Long id) {
        Notice notice = this.getNoticeById(id);
        
        LambdaUpdateWrapper<Notice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notice::getId, id)
                .set(Notice::getDeleted, 1)
                .set(Notice::getUpdateTime, LocalDateTime.now());
        
        this.update(updateWrapper);
        log.info("删除公告成功: id={}", id);
    }

    @Override
    public Notice toggleShow(Long id) {
        Notice notice = this.getNoticeById(id);
        
        // 切换显示状态
        notice.setIsShow(notice.getIsShow() == 1 ? 0 : 1);
        this.updateById(notice);
        
        log.info("切换公告显示状态: id={}, isShow={}", id, notice.getIsShow());
        return this.getNoticeById(id);
    }
}
