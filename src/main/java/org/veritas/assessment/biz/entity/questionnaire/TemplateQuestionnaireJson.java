package org.veritas.assessment.biz.entity.questionnaire;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.constant.QuestionnaireTemplateType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class TemplateQuestionnaireJson {
    private String name;

    private QuestionnaireTemplateType type;

    private String description;

    //    private Map<String, List<TemplateQuestionJson>> questions;
    private Map<Principle, List<TemplateQuestionJson>> questions;

    public Map<Principle, List<TemplateQuestionJson>> getQuestions() {
        if (questions == null || questions.isEmpty()) {
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(questions);
        }
    }

    @Data
    @NoArgsConstructor
    public static class TemplateQuestionJson {
        private String serial;

        private AssessmentStep step;

        private String question;

        private List<String> subQuestionList;

        public List<String> getSubQuestionList() {
            if (subQuestionList == null || subQuestionList.isEmpty()) {
                return Collections.emptyList();
            } else {
                return Collections.unmodifiableList(this.subQuestionList);
            }
        }

        List<TemplateQuestion> toTemplateQuestionList(Principle principle, int serialOfPrinciple, Date editTime) {
            List<TemplateQuestion> templateQuestionList = new ArrayList<>();
            TemplateQuestion templateQuestion = new TemplateQuestion();
            templateQuestion.setPrinciple(principle);
            templateQuestion.setStep(this.step);
            templateQuestion.setContent(this.getQuestion());
            templateQuestion.setSerialOfPrinciple(serialOfPrinciple);
            templateQuestion.setSubSerial(0);
            templateQuestion.setEditable(true);
            templateQuestion.setEditTime(editTime);
            templateQuestionList.add(templateQuestion);
            int subSerial = 1;
            for (String sub : this.getSubQuestionList()) {
                TemplateQuestion subTemplateQuestion = new TemplateQuestion();
                subTemplateQuestion.setPrinciple(principle);
                subTemplateQuestion.setStep(this.step);
                subTemplateQuestion.setContent(sub);
                subTemplateQuestion.setSerialOfPrinciple(serialOfPrinciple);
                subTemplateQuestion.setSubSerial(subSerial);
                ++subSerial;
                subTemplateQuestion.setEditable(true);
                subTemplateQuestion.setEditTime(editTime);
                templateQuestionList.add(subTemplateQuestion);
            }
            return templateQuestionList;
        }
    }

    public TemplateQuestionnaire toTemplateQuestionnaire() {
        Date now = new Date();
        TemplateQuestionnaire templateQuestionnaire = new TemplateQuestionnaire();
        templateQuestionnaire.setName(this.getName());
        templateQuestionnaire.setDescription(this.getDescription());
        templateQuestionnaire.setType(this.getType());
        templateQuestionnaire.setCreatedTime(now);

        List<TemplateQuestion> templateQuestionList = new ArrayList<>();
        for (Map.Entry<Principle, List<TemplateQuestionJson>> entry : this.getQuestions().entrySet()) {
            Principle principle = entry.getKey();
            List<TemplateQuestionJson> questionJsonList = entry.getValue();
            int serialOfPrinciple = 0;
            if (questionJsonList != null && !questionJsonList.isEmpty()) {
                // main
                for (TemplateQuestionJson questionJson : questionJsonList) {
                    templateQuestionList.addAll(questionJson.toTemplateQuestionList(principle, serialOfPrinciple, now));
                    ++serialOfPrinciple;
                }
            }
        }
        templateQuestionnaire.setMainQuestionList(templateQuestionList);
        return templateQuestionnaire;
    }

}