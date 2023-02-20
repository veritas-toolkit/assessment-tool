package org.veritas.assessment.biz.dto.questionnaire;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.system.entity.User;

/**
 * Table of content for questionnaire.
 * <p>
 * Structure: principle -> step -> main question
 */
@Data
@NoArgsConstructor
public class QuestionnaireTocWithMainQuestionDto {

    private QuestionnaireTocDto toc;

    private QuestionDto question;

    public QuestionnaireTocWithMainQuestionDto(QuestionnaireTocDto toc, QuestionDto question) {
        this.toc = toc;
        this.question = question;
    }

    public QuestionnaireTocWithMainQuestionDto(Project project, User user,
                                               QuestionnaireVersion questionnaire, QuestionNode question) {
        this(new QuestionnaireTocDto(questionnaire, project, user), new QuestionDto(question));
    }
}