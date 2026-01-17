package com.syne.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syne.server.common.Result;
import com.syne.server.model.entity.Skill;
import com.syne.server.exception.BusinessException;
import com.syne.server.mapper.SkillMapper;
import com.syne.server.service.SkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 技能管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SkillServiceImpl extends ServiceImpl<SkillMapper, Skill> implements SkillService {

    private final SkillMapper skillMapper;

    @Override
    public List<Skill> getAllSkills() {
        LambdaQueryWrapper<Skill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Skill::getDeleted, 0)
                .orderByAsc(Skill::getSortOrder)
                .orderByDesc(Skill::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public Skill getSkillById(Long id) {
        Skill skill = super.getById(id);
        if (skill == null || skill.getDeleted() == 1) {
            throw new BusinessException("技能不存在");
        }
        return skill;
    }

    @Override
    public Skill createSkill(Skill skill) {
        // 设置默认值
        if (skill.getPercentage() == null) {
            skill.setPercentage(0);
        }
        if (skill.getSortOrder() == null) {
            skill.setSortOrder(0);
        }
        if (skill.getColor() == null) {
            skill.setColor("#3b82f6");
        }
        
        this.save(skill);
        log.info("创建技能成功: id={}, name={}", skill.getId(), skill.getName());
        return skill;
    }

    @Override
    public Skill updateSkill(Long id, Skill skill) {
        Skill existing = this.getSkillById(id);
        
        existing.setName(skill.getName());
        existing.setIcon(skill.getIcon());
        existing.setPercentage(skill.getPercentage());
        existing.setColor(skill.getColor());
        existing.setSortOrder(skill.getSortOrder());
        
        this.updateById(existing);
        log.info("更新技能成功: id={}", id);
        return this.getSkillById(id);
    }

    @Override
    @Transactional
    public Result<String> deleteSkills(String ids) {
        if (!StringUtils.hasText(ids)) {
            throw new BusinessException("技能ID不能为空");
        }

        String[] idArray = ids.split(",");
        List<String> failedIds = new ArrayList<>();
        int successCount = 0;

        for (String idStr : idArray) {
            try {
                Long id = Long.parseLong(idStr.trim());
                this.deleteSkill(id);
                successCount++;
            } catch (NumberFormatException e) {
                failedIds.add(idStr + "(格式错误)");
            } catch (BusinessException e) {
                failedIds.add(idStr + "(" + e.getMessage() + ")");
            }
        }

        String message;
        if (failedIds.isEmpty()) {
            message = successCount == 1 ? "删除技能成功" :
                    String.format("成功删除 %d 个技能", successCount);
        } else {
            message = String.format("成功删除 %d 个技能，失败 %s",
                    successCount, String.join(", ", failedIds));
        }

        return Result.success(message);
    }

    /**
     * 逻辑删除单个技能
     */
    private void deleteSkill(Long id) {
        Skill skill = this.getSkillById(id);
        
        LambdaUpdateWrapper<Skill> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Skill::getId, id)
                .set(Skill::getDeleted, 1)
                .set(Skill::getUpdateTime, LocalDateTime.now());
        
        this.update(updateWrapper);
        log.info("删除技能成功: id={}", id);
    }
}
