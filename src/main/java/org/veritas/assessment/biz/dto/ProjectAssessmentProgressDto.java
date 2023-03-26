package org.veritas.assessment.biz.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectAssessmentProgressDto {
    private int completed;

    private int count;

    public ProjectAssessmentProgressDto(Project project, QuestionnaireVersion questionnaireVersion) {
        Objects.requireNonNull(project);
        Objects.requireNonNull(questionnaireVersion);

        List<Principle> principleList = project.principles();
        if (principleList.isEmpty()) {
            this.completed = 0;
            this.count = 0;
            return;
        }
        int _count = 0;
        int _completed = 0;
        for (Principle principle : principleList) {
            List<QuestionNode> mainList = questionnaireVersion.findMainQuestion(principle);
            _count += mainList.size();
            int completedCount = mainList.stream().mapToInt(main -> main.hasAnswer() ? 1 : 0).sum();
            _completed += completedCount;
        }
        this.setCompleted(_completed);
        this.setCount(_count);
    }
}
