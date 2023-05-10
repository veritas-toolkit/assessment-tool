insert into vat2_questionnaire_version(project_id, model_artifact_vid, creator_user_id, created_time, message)
values (1, null, 1, datetime('now'), 'test');

insert into vat2_question_meta
(id, project_id, main_question_id, current_vid, content_editable, answer_required, add_start_questionnaire_vid)
values (1, 1, 1, 1, false, true, 1);

insert into vat2_question_meta
(id, project_id, main_question_id, current_vid, content_editable, answer_required, add_start_questionnaire_vid)
values (2, 1, 2, 2, false, true, 1);

insert into vat2_question_node
    (questionnaire_vid, project_id, question_id, question_vid, main_question_id, principle, step, serial_of_principle, sub_serial)
values (1, 1, 1, 1, 1, 'F', 0, 0, 0);

insert into vat2_question_node
(questionnaire_vid, project_id, question_id, question_vid, main_question_id, principle, step, serial_of_principle, sub_serial)
values (1, 1, 2, 2, 2, 'F', 0, 1, 0);

insert into vat2_question_version
    (question_id, main_question_id, project_id, content, hint, content_edit_user_id, content_edit_time)
values (1, 1, 1, 'content', 'hint', 1, datetime('now'));
insert into vat2_question_version
(question_id, main_question_id, project_id, content, hint, content_edit_user_id, content_edit_time)
values (2, 2, 1, 'content', 'hint', 1, datetime('now'));

insert into vat2_questionnaire_version
(vid, project_id, creator_user_id, created_time, message)
values (1, 1, 1, datetime('now'), 'created.');