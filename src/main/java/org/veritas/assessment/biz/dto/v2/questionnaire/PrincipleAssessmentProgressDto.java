package org.veritas.assessment.biz.dto.v2.questionnaire;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionVersion;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrincipleAssessmentProgressDto {
    private String principle;

    private int count;

    private int completed;

    public static List<PrincipleAssessmentProgressDto> from(Project project,
                                                            QuestionnaireVersion questionnaireVersion) {
        if (project == null || questionnaireVersion == null) {
            return Collections.emptyList();
        }
        List<Principle> principleList = project.principles();
        if (principleList.isEmpty()) {
            return Collections.emptyList();
        }
        List<PrincipleAssessmentProgressDto> progressDtoList = new ArrayList<>();
        for (Principle principle : principleList) {
            PrincipleAssessmentProgressDto dto = new PrincipleAssessmentProgressDto();
            dto.setPrinciple(principle.getShortName());
            List<QuestionNode> mainList = questionnaireVersion.findMainQuestion(principle);
            dto.setCount(mainList.size());
            int completedCount = mainList.stream().mapToInt(main -> main.hasAnswer() ? 1 : 0).sum();
            dto.setCompleted(completedCount);
            progressDtoList.add(dto);
        }
        return Collections.unmodifiableList(progressDtoList);
    }
}
