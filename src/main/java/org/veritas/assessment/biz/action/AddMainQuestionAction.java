package org.veritas.assessment.biz.action;

import lombok.Data;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.entity.questionnaire.QuestionMeta;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionVersion;
import org.veritas.assessment.system.entity.User;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Data
public class AddMainQuestionAction {
    @NotNull
    private Integer projectId;
    @NotNull
    private Principle principle;
    @NotNull
    private AssessmentStep step;

    @NotNull
    private String question;

    private List<String> subQuestionList;

    private User creator;

    public QuestionNode toNode(Supplier<Long> idSupplier) {
        List<String> allQuestions = new ArrayList<>();
        allQuestions.add(question);
        allQuestions.addAll(subQuestionList);
        List<QuestionNode> questionNodeList = new ArrayList<>(allQuestions.size());
        Long mainQuestionId = null;
        int subSerial = 0;
        for (String q : allQuestions) {
            QuestionMeta meta = new QuestionMeta();
            QuestionVersion questionVersion = new QuestionVersion();
            QuestionNode node = new QuestionNode();
            node.setQuestionVersion(questionVersion);
            node.setMeta(meta);

            Long questionId = idSupplier.get();
            mainQuestionId = mainQuestionId != null ? mainQuestionId : questionId;
            Long questionVid = idSupplier.get();

            meta.setId(questionId);
            meta.setAnswerRequired(false);
            meta.setProjectId(projectId);
            meta.setMainQuestionId(mainQuestionId);
            meta.setCurrentVid(questionVid);

            questionVersion.setVid(questionVid);
            questionVersion.setQuestionId(questionId);
            questionVersion.setContent(q);
            questionVersion.setProjectId(projectId);
            questionVersion.setContentEditable(true);
            questionVersion.setAnswerRequired(false);
            questionVersion.setContentEditUserId(creator.getId());
            questionVersion.setMainQuestionId(mainQuestionId);

            node.setQuestionVid(questionVid);
            node.setMainQuestionId(mainQuestionId);
            node.setQuestionId(questionId);
            node.setProjectId(projectId);
            node.setPrinciple(principle);
            node.setStep(step);
            node.setSubSerial(subSerial);
            ++subSerial;
            questionNodeList.add(node);
        }
        QuestionNode main = questionNodeList.get(0);
        if (subQuestionList != null && !subQuestionList.isEmpty()) {
            List<QuestionNode> subs = questionNodeList.subList(1, questionNodeList.size());
            main.setSubList(subs);
        }
        return main;
    }
}
