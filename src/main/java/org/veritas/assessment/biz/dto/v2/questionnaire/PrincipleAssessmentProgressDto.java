package org.veritas.assessment.biz.dto.v2.questionnaire;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.biz.constant.Principle;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrincipleAssessmentProgressDto {
    private String principle;

    private int count;

    private int completed;

    @Deprecated
    public static List<PrincipleAssessmentProgressDto> testData() {

        List<PrincipleAssessmentProgressDto> progressDtoList = new ArrayList<>();

        progressDtoList.add(new PrincipleAssessmentProgressDto(Principle.G.getDescription(), 0, 13));
        progressDtoList.add(new PrincipleAssessmentProgressDto(Principle.F.getDescription(), 2, 12));
        progressDtoList.add(new PrincipleAssessmentProgressDto(Principle.EA.getDescription(), 6, 8));
        progressDtoList.add(new PrincipleAssessmentProgressDto(Principle.T.getDescription(), 8, 17));
        return progressDtoList;
    }
}
