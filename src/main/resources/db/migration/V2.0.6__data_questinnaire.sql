insert into vat2_template_questionnaire(id, name, description, business_scenario, type, creator_user_id, created_time)
values(1, 'test', 'test template questionnaire', 1, 1, 1, datetime('now'));

insert into vat2_template_question(template_id, principle, step, serial_of_principle, sub_serial,
                                   content, hint, editable, editor_user_id, edit_time, answer_required)
values(1, 'G', 0, 1, 0, 'G1 question content', 'hint', false, 1, datetime('now'), 1);

insert into vat2_template_question(template_id, principle, step, serial_of_principle, sub_serial,
                                   content, hint, editable, editor_user_id, edit_time, answer_required)
values(1, 'G', 0, 2, 0, 'G2 question content', 'hint', false, 1, datetime('now'), 1);

insert into vat2_template_question(template_id, principle, step, serial_of_principle, sub_serial,
                                   content, hint, editable, editor_user_id, edit_time, answer_required)
values(1, 'G', 0, 3, 0, 'G3 question content', 'hint', false, 1, datetime('now'), 1);
insert into vat2_template_question(template_id, principle, step, serial_of_principle, sub_serial,
                                   content, hint, editable, editor_user_id, edit_time, answer_required)
values(1, 'G', 0, 4, 0, 'G4 question content', 'hint', false, 1, datetime('now'), 1);
insert into vat2_template_question(template_id, principle, step, serial_of_principle, sub_serial,
                                   content, hint, editable, editor_user_id, edit_time, answer_required)
values(1, 'G', 1, 5, 0, 'G5 question content', 'hint', false, 1, datetime('now'), 1);
insert into vat2_template_question(template_id, principle, step, serial_of_principle, sub_serial,
                                   content, hint, editable, editor_user_id, edit_time, answer_required)
values(1, 'G', 3, 5, 0, 'G5 question content', 'hint', false, 1, datetime('now'), 1);

insert into vat2_template_question(template_id, principle, step, serial_of_principle, sub_serial,
                                   content, hint, editable, editor_user_id, edit_time, answer_required)
values(1, 'F', 0, 1, 0, 'Question content', 'hint', false, 1, datetime('now'), 1);

insert into vat2_template_question(template_id, principle, step, serial_of_principle, sub_serial,
                                   content, hint, editable, editor_user_id, edit_time, answer_required)
values(1, 'F', 0, 1, 1, 'sub question 1 content', 'hint', false, 1, datetime('now'), 1);

insert into vat2_template_question(template_id, principle, step, serial_of_principle, sub_serial,
                                   content, hint, editable, editor_user_id, edit_time, answer_required)
values(1, 'F', 0, 1, 2, 'sub question 2 content', 'hint', false, 1, datetime('now'), 1);

insert into vat2_template_question(template_id, principle, step, serial_of_principle, sub_serial,
                                   content, hint, editable, editor_user_id, edit_time, answer_required)
values(1, 'F', 0, 1, 3, 'sub question 3 content', 'hint', false, 1, datetime('now'), 1);

insert into vat2_template_question(template_id, principle, step, serial_of_principle, sub_serial,
                                   content, hint, editable, editor_user_id, edit_time, answer_required)
values(1, 'F', 1, 2, 0, 'F2 question content', 'hint', false, 1, datetime('now'), 1);


