package org.veritas.assessment.biz.dto.v2.questionnaire;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.dto.UserSimpleDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.system.entity.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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