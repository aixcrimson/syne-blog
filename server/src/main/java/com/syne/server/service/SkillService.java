package com.syne.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.syne.server.common.Result;
import com.syne.server.entity.Skill;

import java.util.List;

/**
 * 技能管理服务接口
 */
public interface SkillService extends IService<Skill> {

    /**
     * 获取所有技能列表
     * @return 技能列表
     */
    List<Skill> getAllSkills();

    /**
     * 根据ID获取技能
     * @param id 技能ID
     * @return 技能信息
     */
    Skill getSkillById(Long id);

    /**
     * 创建技能
     * @param skill 技能数据
     * @return 创建的技能
     */
    Skill createSkill(Skill skill);

    /**
     * 更新技能
     * @param id 技能ID
     * @param skill 技能数据
     * @return 更新后的技能
     */
    Skill updateSkill(Long id, Skill skill);

    /**
     * 删除技能
     * @param ids 技能ID，多个用逗号分隔
     * @return 删除结果
     */
    Result<String> deleteSkills(String ids);
}
