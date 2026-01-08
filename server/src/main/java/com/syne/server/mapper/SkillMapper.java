package com.syne.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syne.server.entity.Skill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SkillMapper extends BaseMapper<Skill> {

    /**
     * 查询所有技能
     * @return 列表
     */
    @Select("SELECT * FROM skills WHERE deleted = 0 ORDER BY sort_order DESC")
    List<Skill> selectAllSkills();
}
