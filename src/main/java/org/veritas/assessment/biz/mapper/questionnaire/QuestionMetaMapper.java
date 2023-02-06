package org.veritas.assessment.biz.mapper.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.QuestionMeta;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;

import java.util.List;

@Repository
@CacheConfig(cacheNames = "vat2_question_meta")
public interface QuestionMetaMapper extends BaseMapper<QuestionMeta> {
    Logger log = LoggerFactory.getLogger(QuestionNodeMapper.class);

    default int saveAll(List<QuestionMeta> list) {
        if (list == null || list.isEmpty()) {
            log.warn("arg list is null or empty");
            return 0;
        }
        return list.stream()
                .mapToInt(this::insert)
                .sum();
    }

    default int addMainQuestionWithSubs(QuestionMeta main) {
        int result = insert(main);

        return result;
    }

    // select list by projectId.
    default List<QuestionMeta> findByProjectId(int projectId) {
        LambdaQueryWrapper<QuestionMeta> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionMeta::getProjectId, projectId);
        wrapper.orderByAsc(QuestionMeta::getId);
        return selectList(wrapper);
    }

    default boolean updateVersionId(long questionId, long oldVid, long newVid) {
        LambdaUpdateWrapper<QuestionMeta> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(QuestionMeta::getId, questionId);
        wrapper.eq(QuestionMeta::getCurrentVid, oldVid);
        wrapper.isNotNull(QuestionMeta::getDeleteStartQuestionnaireVid);
        wrapper.set(QuestionMeta::getCurrentVid, newVid);
        return update(null, wrapper) > 0;
    }
    // select list by main-question id.

    // soft delete.
    // update deleteStartQuestionnaireVid to not null value
    // need project id;


}