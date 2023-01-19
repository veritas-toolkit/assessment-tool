package org.veritas.assessment.biz.mapper.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.QuestionMeta;

import java.util.List;

@Repository
@CacheConfig(cacheNames = "vat2_question_meta")
public interface QuestionMetaMapper extends BaseMapper<QuestionMeta> {


    // select list by projectId.
    default List<QuestionMeta> findByProjectId(int projectId) {
        LambdaQueryWrapper<QuestionMeta> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionMeta::getProjectId, projectId);
        wrapper.orderByAsc(QuestionMeta::getId);
        return selectList(wrapper);
    }

    // select list by main-question id.

    // soft delete.
    // update deleteStartQuestionnaireVid to not null value
    // need project id;


}