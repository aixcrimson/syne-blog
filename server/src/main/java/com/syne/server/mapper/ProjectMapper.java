package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    /**
     * 查询精选项目
     * @return 列表
     */
    @Select("SELECT * FROM projects WHERE is_featured = 1 AND deleted = 0 ORDER BY sort_order DESC")
    List<Project> selectFeaturedProjects();
    
    /**
     * 查询所有项目
     * @return 列表
     */
    @Select("SELECT * FROM projects WHERE deleted = 0 ORDER BY sort_order DESC")
    List<Project> selectAllProjects();
}
