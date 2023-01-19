package org.veritas.assessment.biz.service.questionnaire;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.biz.mapper.questionnaire.TemplateQuestionnaireDao;
import org.veritas.assessment.common.metadata.Pageable;

import java.util.List;

@Service
@Slf4j
public class TemplateQuestionnaireService {
    @Setter
    @Autowired
    private TemplateQuestionnaireDao templateQuestionnaireDao;

    // all list
    @Transactional(readOnly = true)
    public List<TemplateQuestionnaire> findAllTemplateBasic() {
        return templateQuestionnaireDao.findAllWithoutQuestion();
    }

    @Transactional(readOnly = true)
    public Pageable<TemplateQuestionnaire> findTemplatePageable(String prefix, String keyword, Integer businessScenario,
                                                                int page, int pageSize) {
        return templateQuestionnaireDao.findTemplatePageable(prefix, keyword, businessScenario, page, pageSize);
    }

    // find by template id
    @Transactional(readOnly = true)
    public TemplateQuestionnaire findByTemplateId(int templateId) {
        return templateQuestionnaireDao.findById(templateId);
    }

    // create new template from another

    // delete the template.

    // update basic information.

    // update question content

    // delete question(main or sub)

}
