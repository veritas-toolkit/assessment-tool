package org.veritas.assessment.biz.mapper.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;

@Repository
public interface QuestionnaireVersionMapper extends BaseMapper<QuestionnaireVersion> {

    default QuestionnaireVersion findByQuestionnaireVid(long vid) {
        return selectById(vid);
    }

    default QuestionnaireVersion findLatest(int projectId) {
        LambdaQueryWrapper<QuestionnaireVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionnaireVersion::getProjectId, projectId);
        wrapper.orderByDesc(QuestionnaireVersion::getVid);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }

    default int updateAsExported(long vid) {
        LambdaUpdateWrapper<QuestionnaireVersion> wrapper = new LambdaUpdateWrapper<QuestionnaireVersion>();
        wrapper.eq(QuestionnaireVersion::getVid, vid);
        wrapper.set(QuestionnaireVersion::getExported, true);
        return update(null, wrapper);
    }





}
