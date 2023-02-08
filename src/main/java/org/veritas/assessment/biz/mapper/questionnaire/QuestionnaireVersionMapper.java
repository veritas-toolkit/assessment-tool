package org.veritas.assessment.biz.mapper.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;

@Repository
public interface QuestionnaireVersionMapper extends BaseMapper<QuestionnaireVersion> {

    @Select("select max(vid) from vat2_questionnaire_version qa_v where qa_v.project_id = #{projectId}")
    Integer findLatestVersionId(@Param("projectId") int projectId);

    default QuestionnaireVersion findByQuestionnaireVid(long vid) {
        return selectById(vid);
    }

    default QuestionnaireVersion findLatest(int projectId) {
        LambdaQueryWrapper<QuestionnaireVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionnaireVersion::getProjectId, projectId);
        wrapper.orderByDesc(QuestionnaireVersion::getVid);
        wrapper.last("limit 1");
        return selectOne(wrapper);
//        Integer latestVid = findLatestVersionId(projectId);
//        if (latestVid == null ) {
//            return null;
//        }
//        LambdaQueryWrapper<QuestionnaireVersion> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(QuestionnaireVersion::getVid, latestVid);
//        return selectOne(wrapper);
    }




}
