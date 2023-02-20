package org.veritas.assessment.biz.mapper.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.common.metadata.Pageable;

import java.util.Collections;
import java.util.List;

@Repository
public interface TemplateQuestionnaireMapper extends BaseMapper<TemplateQuestionnaire> {

    default List<TemplateQuestionnaire> findAll() {
        LambdaQueryWrapper<TemplateQuestionnaire> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(TemplateQuestionnaire::getType);
        wrapper.orderByAsc(TemplateQuestionnaire::getCreatedTime);
        return selectList(wrapper);
    }

    default List<TemplateQuestionnaire> findByBusinessScenario(BusinessScenarioEnum businessScenarioEnum) {
        LambdaQueryWrapper<TemplateQuestionnaire> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TemplateQuestionnaire::getBusinessScenario, businessScenarioEnum.getCode());
        wrapper.orderByDesc(TemplateQuestionnaire::getId);
        wrapper.orderByAsc(TemplateQuestionnaire::getType);
        wrapper.orderByAsc(TemplateQuestionnaire::getCreatedTime);
        return selectList(wrapper);
    }


    default Pageable<TemplateQuestionnaire> findTemplatePageable(String prefix, String keyword,
                                                                 Integer businessScenario,
                                                                 int page, int pageSize) {
        LambdaQueryWrapper<TemplateQuestionnaire> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(prefix)) {
            wrapper.apply("upper(name) like {0}", StringUtils.upperCase(prefix) + "%");
        }
        if (StringUtils.isNotEmpty(keyword)) {
            String v = "%" + StringUtils.upperCase(keyword) + "%";
            wrapper.apply("upper(name) like {0} or upper(description) like {1}", v, v);
        }
        if (businessScenario != null) {
            wrapper.eq(TemplateQuestionnaire::getBusinessScenario, businessScenario);
        }

        wrapper.orderByDesc(TemplateQuestionnaire::getCreatedTime);
        Page<TemplateQuestionnaire> projectPage = new Page<>();
        projectPage.setCurrent(page);
        projectPage.setSize(pageSize);
        Page<TemplateQuestionnaire> page1 = selectPage(projectPage, wrapper);
        return Pageable.convert(page1);
    }

    

}
