/*
 * Copyright 2021 MAS Veritas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.veritas.assessment.biz.mapper.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.common.metadata.Pageable;

import java.util.List;

@Repository
@Deprecated
public interface TemplateQuestionnaireMapper extends QuestionnaireValueMapper<TemplateQuestionnaire> {

    default Pageable<TemplateQuestionnaire> findTemplatePageable(String prefix, String keyword,
                                                                 int page, int pageSize) {
        LambdaQueryWrapper<TemplateQuestionnaire> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(prefix)) {
            wrapper.apply("upper(name) like {0}", StringUtils.upperCase(prefix) + "%");
        }
        if (StringUtils.isNotEmpty(keyword)) {
            wrapper.apply("upper(name) like {0}", "%" + StringUtils.upperCase(keyword) + "%");
        }

        wrapper.orderByDesc(TemplateQuestionnaire::getCreatedTime);
        Page<TemplateQuestionnaire> projectPage = new Page<>();
        projectPage.setCurrent(page);
        projectPage.setSize(pageSize);
        Page<TemplateQuestionnaire> page1 = selectPage(projectPage, wrapper);
        return Pageable.convert(page1);
    }


    default List<TemplateQuestionnaire> findTemplateList() {
        LambdaQueryWrapper<TemplateQuestionnaire> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(TemplateQuestionnaire::getCreatedTime);
        return selectList(wrapper);
    }

    default int updateBasicInfo(Integer templateId, String name, String description) {
        LambdaUpdateWrapper<TemplateQuestionnaire> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TemplateQuestionnaire::getTemplateId, templateId);
        wrapper.set(TemplateQuestionnaire::getName, name);
        wrapper.set(TemplateQuestionnaire::getDescription, description);
        return update(null, wrapper);
    }
}
