package org.veritas.assessment.biz.service.questionnaire;

import org.veritas.assessment.biz.action.AddMainQuestionAction;
import org.veritas.assessment.biz.action.AddSubQuestionAction;
import org.veritas.assessment.biz.action.DeleteSubQuestionAction;
import org.veritas.assessment.biz.action.EditAnswerAction;
import org.veritas.assessment.biz.action.EditMainQuestionAction;
import org.veritas.assessment.biz.action.EditSubQuestionAction;
import org.veritas.assessment.biz.action.ReorderSubQuestionAction;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.entity.User;

import java.util.Date;
import java.util.List;

public interface QuestionnaireService {


    // find the latest version by project's id.
    QuestionnaireVersion findLatestQuestionnaire(int projectId);

    QuestionnaireVersion findByQuestionnaireVid(long vid);

    // find by version id.

    // create a new one from another project.
    // check the biz
    QuestionnaireVersion copyFrom(int creatorUserId, Project project, Date createdTime, Project copyFrom);

    // create a new one by the template questionnaire.
    // check the biz
    QuestionnaireVersion createByTemplate(int creatorUserId, Project project, Date createdTime, TemplateQuestionnaire template);

    // copy from
//    QuestionnaireVersion createByProject(int userId, Project project, Date createdTime ,int projectId);

    void saveNewQuestionnaire(QuestionnaireVersion questionnaireVersion);

    // edit answer

    QuestionnaireVersion editAnswer(User operator, Integer projectId, EditAnswerAction editAnswerAction);
    QuestionnaireVersion editAnswer(User operator, Project project, List<EditAnswerAction> editAnswerActionList, ModelArtifact modelArtifact);

    Pageable<QuestionnaireVersion> findHistory(int projectId, boolean exportedOnly, boolean draftOnly,
                                               int page, int pageSize);

    QuestionnaireVersion addMainQuestion(AddMainQuestionAction action);

    QuestionnaireVersion deleteMainQuestion(User operator, Project project, Long questionId);

    QuestionnaireVersion editMainQuestion(EditMainQuestionAction action);

    QuestionnaireVersion reorderQuestion(User operator, Integer projectId, Principle principle, AssessmentStep step, List<Long> questionIdList);

    QuestionnaireVersion addSubQuestion(AddSubQuestionAction action);

    QuestionnaireVersion deleteSubQuestion(DeleteSubQuestionAction action);

    QuestionnaireVersion editSubQuestion(EditSubQuestionAction action);

    QuestionnaireVersion reorderSubQuestion(ReorderSubQuestionAction action);

    int updateAsExported(long vid);
}
