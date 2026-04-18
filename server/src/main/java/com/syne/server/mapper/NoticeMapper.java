package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.model.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * 查询显示的公告列表
     * @return 列表
     */
    @Select("SELECT * FROM notices WHERE is_show = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<Notice> selectShowNotices();
}
