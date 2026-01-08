package com.syne.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.syne.server.common.Result;
import com.syne.server.entity.Notice;

import java.util.List;

/**
 * 公告管理服务接口
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 获取所有公告列表
     * @return 公告列表
     */
    List<Notice> getAllNotices();

    /**
     * 根据ID获取公告
     * @param id 公告ID
     * @return 公告信息
     */
    Notice getNoticeById(Long id);

    /**
     * 创建公告
     * @param notice 公告数据
     * @return 创建的公告
     */
    Notice createNotice(Notice notice);

    /**
     * 更新公告
     * @param id 公告ID
     * @param notice 公告数据
     * @return 更新后的公告
     */
    Notice updateNotice(Long id, Notice notice);

    /**
     * 删除公告
     * @param ids 公告ID，多个用逗号分隔
     * @return 删除结果
     */
    Result<String> deleteNotices(String ids);

    /**
     * 切换公告显示状态
     * @param id 公告ID
     * @return 更新后的公告
     */
    Notice toggleShow(Long id);
}
