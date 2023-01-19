package org.veritas.assessment.biz.mapper.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.QuestionVersion;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
//@CacheConfig(cacheNames = "vat2_question_version")
public interface QuestionVersionMapper extends BaseMapper<QuestionVersion> {

    // select by id list
    default List<QuestionVersion> findByIdList(Collection<Integer> allVid) {
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