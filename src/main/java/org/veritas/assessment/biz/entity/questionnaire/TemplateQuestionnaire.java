package org.veritas.assessment.biz.entity.questionnaire;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.constant.QuestionnaireTemplateType;
import org.veritas.assessment.biz.dto.questionnaire.QuestionnaireDiffTocDto;
import org.veritas.assessment.common.exception.IllegalRequestException;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@TableName(autoResultMap = true)
public class TemplateQuestionnaire {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private BusinessScenarioEnum businessScenario;

    private QuestionnaireTemplateType type;

    private String description;

    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date createdTime;

    private Integer creatorUserId;

    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date editTime;

    private Integer editUserId;

    @TableLogic
    private boolean deleted = false;

    @TableField(exist = false)
    private List<TemplateQuestion> mainQuestionList;

    public List<TemplateQuestion> getMainQuestionList() {
        if (mainQuestionList == null || mainQuestionList.isEmpty()) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(mainQuestionList);
        }
    }

    public void addAllQuestionList(List<TemplateQuestion> questionList) {
        if (questionList == null || questionList.isEmpty()) {
            this.mainQuestionList = Collections.emptyList();
        }
        this.mainQuestionList = TemplateQuestion.toStructure(questionList);
    }

    public void addMainQuestion(TemplateQuestion main) {
        List<TemplateQuestion> list = findMainQuestionListByPrinciple(main.getPrinciple());
        for (TemplateQuestion question : list) {
            if (question.getSerialOfPrinciple() >= main.getSerialOfPrinciple()) {
                question.setSerialOfPrinciple(question.getSerialOfPrinciple() + 1);
            }
        }
        List<TemplateQuestion> allMainList = new ArrayList<>(this.getMainQuestionList());
        allMainList.add(main);
        this.mainQuestionList = fixStructure(allMainList);
    }

    public List<TemplateQuestion> allQuestionList() {
        List<TemplateQuestion> list = new ArrayList<>();
        for (TemplateQuestion templateQuestion : this.mainQuestionList) {
            list.add(templateQuestion);
            list.addAll(templateQuestion.getSubList());
        }
        return Collections.unmodifiableList(list);
    }

    public void setId(Integer id) {
        Objects.requireNonNull(id);
        this.id = id;
        this.getMainQuestionList().forEach(q -> {
            q.setTemplateId(id);
            q.getSubList().forEach(sub -> sub.setTemplateId(id));
        });
    }

    public boolean cannotBeEditOrDeleted() {
        return this.type == QuestionnaireTemplateType.SYSTEM;
    }

    public TemplateQuestion findQuestion(Integer questionId) {
        for (TemplateQuestion question : this.mainQuestionList) {
            if (Objects.equals(question.getId(), questionId)) {
                return question;
            }
            for (TemplateQuestion sub : question.getSubList()) {
                if (Objects.equals(sub.getId(), questionId)) {
                    return sub;
                }
            }
        }
        return null;
    }

    public List<TemplateQuestion> findMainQuestionListByPrinciple(Principle principle) {
        return this.getMainQuestionList().stream()
                .filter(q -> q.getPrinciple() == principle)
                .collect(Collectors.toList());
    }
    public List<TemplateQuestion> findMainQuestionListByPrincipleStep(Principle principle, AssessmentStep step) {
        Objects.requireNonNull(principle);
        Objects.requireNonNull(step);
        return this.getMainQuestionList().stream()
                .filter(q -> q.getPrinciple() == principle && q.getStep() == step)
                .collect(Collectors.toList());
    }

    public TemplateQuestion findMainBySerial(String serial) {
        return this.getMainQuestionList().stream()
                .filter(e -> StringUtils.equals(e.serial(), serial))
                .findFirst().orElse(null);
    }

    public void deleteMainQuestion(Integer questionId) {
        List<TemplateQuestion> list = this.getMainQuestionList().stream()
                .filter(q -> !Objects.equals(q.getId(), questionId))
                .collect(Collectors.toList());
        this.mainQuestionList = fixStructure(list);
    }

    public int deleteSubQuestion(Integer questionId, Integer subQuestionId) {
        TemplateQuestion main = this.findQuestion(questionId);
        if (main == null) {
            return 0;
        }
        List<TemplateQuestion> newSubList = main.getSubList().stream()
                .filter(s -> !Objects.equals(s.getId(), subQuestionId))
                .collect(Collectors.toList());
        if (newSubList.size() == main.getSubList().size()) {
            return 0;
        }
        int subSeq = 1;
        for (TemplateQuestion sub : newSubList) {
            sub.setSubSerial(subSeq);
            ++subSeq;
        }
        main.setSubList(newSubList);
        return 1;
    }

    public void reorderMainQuestion(Principle principle, AssessmentStep step, List<Integer> newOrderList) {
        List<TemplateQuestion> oldList = this.findMainQuestionListByPrincipleStep(principle, step);
        if (oldList == null || oldList.isEmpty()) {
            return;
        }
        List<TemplateQuestion> newList = new ArrayList<>(oldList.size());
        for (Integer id : newOrderList) {
            for (TemplateQuestion question : oldList) {
                if (Objects.equals(question.getId(), id)) {
                    newList.add(question);
                    break;
                }
            }
        }
        if (oldList.size() != newList.size()) {
            throw new IllegalRequestException("The questionnaire has been changed.");
        }
        int sop = oldList.get(0).getSerialOfPrinciple();
        for (TemplateQuestion question : newList) {
            question.setSerialOfPrinciple(sop);
            ++sop;
        }
        this.mainQuestionList = fixStructure(mainQuestionList);
    }


    private static List<TemplateQuestion> fixStructure(List<TemplateQuestion> questionList) {
        if (questionList == null || questionList.isEmpty()) {
            return Collections.emptyList();
        }
        List<TemplateQuestion> list = questionList.stream()
                .sorted((a, b) -> {
                    int result = a.getPrinciple().compareTo(b.getPrinciple());
                    if (result != 0) {
                        return result;
                    }
                    result = a.getStep().compareTo(b.getStep());
                    if (result != 0) {
                        return result;
                    }
                    result = a.getSerialOfPrinciple() - b.getSerialOfPrinciple();
                    if (result != 0) {
                        return result;
                    }
                    return a.getSubSerial() - b.getSubSerial();
                }).collect(Collectors.toList());
        List<TemplateQuestion> result = new ArrayList<>(list.size());
        for (Principle principle : Principle.values()) {
            int seq = 1;
            for (TemplateQuestion question : list) {
                if (principle == question.getPrinciple()) {
                    question.setSerialOfPrinciple(seq);
                    ++seq;
                    question.setSubSerial(0);
                    int subSeq = 1;
                    for (TemplateQuestion sub : question.getSubList()) {
                        sub.setSubSerial(subSeq);
                        ++subSeq;
                    }
                    result.add(question);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

}