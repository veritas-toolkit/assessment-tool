package org.veritas.assessment.biz.mapper.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.List;
import java.util.Objects;

@Repository
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

    default int saveAll(List<TemplateQuestion> templateQuestionList) {
        if (templateQuestionList == null || templateQuestionList.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (TemplateQuestion main : templateQuestionList) {
            result += this.insert(main);
            main.setMainQuestionId(main.getId());
            LambdaUpdateWrapper<TemplateQuestion> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(TemplateQuestion::getId, main.getId());
            wrapper.set(TemplateQuestion::getMainQuestionId, main.getMainQuestionId());
            this.update(null, wrapper);
            for (TemplateQuestion sub : main.getSubList()) {
                result += this.insert(sub);
            }
        }
        return result;
    }

    default int updateContent(TemplateQuestion templateQuestion) {
        Objects.requireNonNull(templateQuestion);
        LambdaUpdateWrapper<TemplateQuestion> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TemplateQuestion::getId, templateQuestion.getId());
        wrapper.set(TemplateQuestion::getContent, templateQuestion.getContent());
        wrapper.set(TemplateQuestion::getEditTime, TimestampHandler.toDbString(templateQuestion.getEditTime()));
        wrapper.set(TemplateQuestion::getEditorUserId, templateQuestion.getEditorUserId());
        return update(null, wrapper);
    }

    default int updateStructureInfo(TemplateQuestion mainQuestion) {
        int result = 0;
        for (TemplateQuestion question : mainQuestion.plainList()) {
            LambdaUpdateWrapper<TemplateQuestion> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(TemplateQuestion::getId, question.getId());
            wrapper.set(TemplateQuestion::getSerialOfPrinciple, question.getSerialOfPrinciple());
            wrapper.set(TemplateQuestion::getSubSerial, question.getSubSerial());
            result += update(null, wrapper);
        }
        return result;


    }
    default int updateStructureInfo(List<TemplateQuestion> questionList) {
        if (questionList == null || questionList.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (TemplateQuestion question : questionList) {
            result += updateStructureInfo(question);
        }
        return result;
    }

}
