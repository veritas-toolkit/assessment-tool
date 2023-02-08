package org.veritas.assessment.biz.service.questionnaire;

import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.common.metadata.Pageable;

import java.util.Date;

public interface QuestionnaireService {


    // find the latest version by project's id.
    QuestionnaireVersion findLatestQuestionnaire(int projectId);

    // find by version id.

    // create a new one from another project.
    // check the biz
    QuestionnaireVersion copyFrom(int creatorUserId, Project project, Date createdTime , Project copyFrom);

    // create a new one by the template questionnaire.
    // check the biz
    QuestionnaireVersion createByTemplate(int creatorUserId, Project project, Date createdTime, TemplateQuestionnaire template);

    // copy from
//    QuestionnaireVersion createByProject(int userId, Project project, Date createdTime ,int projectId);

    void saveNewQuestionnaire(QuestionnaireVersion questionnaireVersion);

    // edit answer

    QuestionnaireVersion editAnswer(int projectId, long questionId, long basedQuestionVid, String answer, int editorId);

    Pageable<QuestionnaireVersion> findHistory(int projectId, int page, int pageSize);

}
