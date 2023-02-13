package org.veritas.assessment.biz.action;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.veritas.assessment.biz.constant.QuestionContentEditType;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.common.exception.IllegalRequestException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.system.entity.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class EditMainQuestionAction {
    @NotNull
    private Project projectId;

    @NotNull
    private QuestionnaireVersion basedQuestionnaire;

    @NotNull
    private Long mainQuestionId;

    private String mainQuestion;

    @NotNull
    private User editor;

    List<Long> deletedSubList;

    private List<SubQuestion> subQuestionList;


    @Data
    public static class SubQuestion {
        private Long questionId;

        private Long basedQuestionVid;

        private String question;

        @NotNull
        private QuestionContentEditType editType;
    }

    @Null
    public List<Long> getDeletedSubList() {
        if (deletedSubList == null) {
            return Collections.emptyList();
        } else {
            return deletedSubList;
        }
    }

    @Null
    public List<SubQuestion> getSubQuestionList() {
        return subQuestionList == null ? Collections.emptyList() : subQuestionList;
    }

    public Map<Integer, String> getAddSubQuestionList() {
        Map<Integer, String> map = new LinkedHashMap<>();
        int subSerial = 0;
        for (SubQuestion subQuestion : this.getSubQuestionList()) {
            ++subSerial;
            if (subQuestion.getEditType() == QuestionContentEditType.ADD) {
                map.put(subSerial, subQuestion.getQuestion());
            }
        }
        return map;
    }

    public Map<Long, Integer> toSubSerialMap() {
        Map<Long, Integer> map = new LinkedHashMap<>();
        int subSerial = 1;
        for (SubQuestion subQuestion : this.getSubQuestionList()) {

            if (subQuestion.getEditType() == QuestionContentEditType.UNMODIFIED ||
                    subQuestion.getEditType() == QuestionContentEditType.MODIFIED) {
                map.put(subQuestion.getQuestionId(), subSerial);
                ++subSerial;
            }
        }
        return Collections.unmodifiableMap(map);

    }

    // including main question.
    public Map<Long, String> toEditQuestionMap() {
        Map<Long, String> map = new LinkedHashMap<>();

        QuestionNode basedMain = this.getBasedQuestionnaire().findMainQuestionById(this.getMainQuestionId());
        if (basedMain == null) {
            throw new NotFoundException("Not found the question.");
        }
        if (!StringUtils.equals(this.getMainQuestion(), basedMain.questionContent())) {
            map.put(this.getMainQuestionId(), this.getMainQuestion());
        }

        for (SubQuestion subQuestion : this.getSubQuestionList()) {
            if (subQuestion.getEditType() == QuestionContentEditType.MODIFIED) {
                map.put(subQuestion.getQuestionId(), subQuestion.getQuestion());
            }
        }
        return Collections.unmodifiableMap(map);
    }

    public void valid() {
        QuestionNode main = basedQuestionnaire.findMainQuestionById(mainQuestionId);
        List<Long> subQuestionIdList = new LinkedList<>(main.subQuestionIdList());
        int deletedCount = this.getDeletedSubList().size();
        for (Long id : this.getDeletedSubList()) {
            if (!subQuestionIdList.remove(id)) {
                log.warn("The question[{}] has been deleted.");
            }
        }

        int existCount = 0;
        for (SubQuestion s : this.getSubQuestionList()) {
            if (s.getEditType() == QuestionContentEditType.UNMODIFIED
                    || s.editType == QuestionContentEditType.MODIFIED) {
                ++existCount;
                if (!subQuestionIdList.contains(s.getQuestionId())) {
                    throw new NotFoundException("Not found the sub question.");
                }
            }
        }
        if (subQuestionIdList.size() != existCount) {
            throw new IllegalRequestException();
        }

    }
}
