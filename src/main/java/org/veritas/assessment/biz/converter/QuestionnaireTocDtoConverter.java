package org.veritas.assessment.biz.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.veritas.assessment.biz.dto.questionnaire.QuestionnaireTocDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

@Component
@Slf4j
public class QuestionnaireTocDtoConverter implements Converter<QuestionnaireTocDto, QuestionnaireVersion> {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Override
    public QuestionnaireTocDto convertFrom(QuestionnaireVersion q) {
        User user = userService.findUserById(q.getCreatorUserId());
        Project project = projectService.findProjectById(q.getProjectId());
        return new QuestionnaireTocDto(q, project, user);
    }
}
