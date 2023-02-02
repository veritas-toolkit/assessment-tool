package org.veritas.assessment.biz.mapper.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.QuestionMeta;
import org.veritas.assessment.biz.entity.questionnaire.QuestionVersion;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
//@CacheConfig(cacheNames = "vat2_question_version")
public interface QuestionVersionMapper extends BaseMapper<QuestionVersion> {
    Logger log = LoggerFactory.getLogger(QuestionNodeMapper.class);

    default int saveAll(List<QuestionVersion> list) {
        if (list == null || list.isEmpty()) {
            log.warn("arg list is null or empty");
            return 0;
        }
        return list.stream()
                .mapToInt(this::insert)
                .sum();
    }
    // select by question vid Collection.
    default List<QuestionVersion> findByIdList(Collection<Long> allVid) {
        if (allVid == null || allVid.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<QuestionVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(QuestionVersion::getVid, allVid);
        return selectList(wrapper);
    }


    // find the latest version of question by q_id

    QuestionVersion findLatestVersion(int questionId);

    // 如果当前问题没有被更新，则新增一条新版本记录
    int addBasedLatest(QuestionVersion questionVersion, int basedVid);


}