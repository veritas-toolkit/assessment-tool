package org.veritas.assessment.biz.mapper.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersionStructure;

import java.util.List;

@Repository
@CacheConfig(cacheNames = "")
public interface QuestionnaireVersionStructureMapper extends BaseMapper<QuestionnaireVersionStructure> {
    // 根据 Questionnaire vid 获取整个 Structure
    default List<QuestionnaireVersionStructure> findByVid(int vid) {
        LambdaQueryWrapper<QuestionnaireVersionStructure> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionnaireVersionStructure::getQuestionnaireVid, vid);
        wrapper.orderByAsc(QuestionnaireVersionStructure::getPrinciple);
        wrapper.orderByAsc(QuestionnaireVersionStructure::getStep);
        wrapper.orderByAsc(QuestionnaireVersionStructure::getSerialOfPrinciple);
        wrapper.orderByAsc(QuestionnaireVersionStructure::getSubSerial);
        return selectList(wrapper);
    }

    // 根据 projectId 获取最新的 Structure
    // findLatestByPid


    // saveStructureList

}