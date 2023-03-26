package org.veritas.assessment.biz.mapper.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@CacheConfig(cacheNames = "vat2_question_node")
public interface QuestionNodeMapper extends BaseMapper<QuestionNode> {
    Logger log = LoggerFactory.getLogger(QuestionNodeMapper.class);

    default List<QuestionNode> findByQuestionnaireVid(long questionnaireVid) {
        LambdaQueryWrapper<QuestionNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionNode::getQuestionnaireVid, questionnaireVid);
        List<QuestionNode> list = selectList(wrapper);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().sorted().collect(Collectors.toList());
    }

    default int saveAll(List<QuestionNode> questionNodeList) {
        if (questionNodeList == null || questionNodeList.isEmpty()) {
            log.warn("arg list is null or empty");
            return 0;
        }
        return questionNodeList.stream()
                .mapToInt(this::insert)
                .sum();
    }
}