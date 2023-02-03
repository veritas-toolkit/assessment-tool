package org.veritas.assessment.biz.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.veritas.assessment.biz.dto.UserSimpleDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionnaireTocDto;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

@Component
@Slf4j
public class QuestionnaireTocDtoConverter implements Converter<QuestionnaireTocDto, QuestionnaireVersion> {

    @Autowired
    private UserService userService;


    @Override
    public QuestionnaireTocDto convertFrom(QuestionnaireVersion q) {
        QuestionnaireTocDto dto = new QuestionnaireTocDto();
        dto.setProjectId(q.getProjectId());
        dto.setQuestionnaireVid(q.getVid());
        dto.setCreatedTime(q.getCreatedTime());

        User user = userService.findUserById(q.getCreatorUserId());
        dto.setCreator(new UserSimpleDto(user));
        dto.fillQuestions(q.getMainQuestionNodeList());
        return dto;
    }
}
