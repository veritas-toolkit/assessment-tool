package org.veritas.assessment.biz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Questionnaire for generating report.
 */
@Slf4j
public class ReportQuestionnaire {

    @Getter
    private final Project project;

    @Getter
    private final QuestionnaireVersion questionnaire;

    public ReportQuestionnaire(Project project, QuestionnaireVersion questionnaire) {
        this.project = project;
        this.questionnaire = questionnaire;
    }

    public List<Principle> principles() {
        return project.principles();
    }

    public List<AssessmentStep> steps(Principle principle) {
        List<QuestionNode> questionNodeList = questionnaire.findMainQuestion(principle);
        Set<AssessmentStep> set = new HashSet<>();
        questionNodeList.forEach(q -> set.add(q.getStep()));
        return Arrays.stream(AssessmentStep.values())
                .filter(set::contains)
                .collect(Collectors.toList());
    }

    public boolean hasQuestions(Principle principle, AssessmentStep step) {
        List<QuestionNode> questionNodeList = questionnaire.find(principle, step);
        return questionNodeList != null && !questionNodeList.isEmpty();
    }

    public List<MainQuestion> findQuestionList(Principle principle, AssessmentStep step) {
        List<QuestionNode> questionNodeList = questionnaire.find(principle, step);
        if(questionNodeList == null || questionNodeList.isEmpty()) {
            return Collections.emptyList();
        }
        return questionNodeList.stream().map(MainQuestion::new).collect(Collectors.toList());

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainQuestion {
        private String question;
        private String answer;
        private List<SubQuestion> subQuestionList;

        public MainQuestion(QuestionNode questionNode) {
            this.question = questionNode.getQuestionVersion().getContent();
            this.answer = questionNode.getQuestionVersion().getAnswer();
            this.subQuestionList = questionNode.getSubList().stream()
                    .map(SubQuestion::new)
                    .collect(Collectors.toList());
        }

        public boolean hasSubs() {
            return subQuestionList != null && !subQuestionList.isEmpty();
        }

        public boolean hasAnswer() {
            return !StringUtils.isEmpty(answer);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubQuestion {
        private String question;
        private String answer;

        public SubQuestion(QuestionNode questionNode) {
            this.question = questionNode.getQuestionVersion().getContent();
            this.answer = questionNode.getQuestionVersion().getAnswer();
        }

        public boolean hasAnswer() {
            return !StringUtils.isEmpty(answer);
        }
    }


}
