package org.veritas.assessment.biz.mapper.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;

import java.util.List;

@Repository(value = "TemplateQuestionMapper2")
public interface TemplateQuestionMapper extends BaseMapper<TemplateQuestion> {

    default List<TemplateQuestion> findByTemplateId(int templateId) {
        LambdaQueryWrapper<TemplateQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TemplateQuestion::getTemplateId, templateId);
        wrapper.orderByAsc(TemplateQuestion::getPrinciple);
        wrapper.orderByAsc(TemplateQuestion::getStep);
        wrapper.orderByAsc(TemplateQuestion::getSerialOfPrinciple);
        wrapper.orderByAsc(TemplateQuestion::getSubSerial);
        return selectList(wrapper);
    }
}
