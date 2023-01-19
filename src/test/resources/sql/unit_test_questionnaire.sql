insert into vat2_questionnaire_version(project_id, model_artifact_vid, creator_user_id, created_time, message)
values (1, null, 1, datetime('now'), 'test');

insert into vat2_question_meta
    (project_id, main_question_id, editable, answer_required, add_start_questionnaire_vid)
values (1, 1, 1, 1, 1);

insert into vat2_question_meta
(project_id, main_question_id, editable, answer_required, add_start_questionnaire_vid)
values (1, 1, 2, 1, 1);

insert into vat2_questionnaire_version_structure
    (questionnaire_vid, project_id, question_id, question_vid, main_question_vid, principle,
     step, serial_of_principle, sub_serial)
values (1, 1, 1, 1, 1, 'F',
        1, 1, 0);

insert into vat2_question_version
    (question_id, main_question_id, project_id, content, hint, content_edit_user_id, content_edit_time)
values (1, 1, 1, 'content', 'hint', 1, datetime('now'));