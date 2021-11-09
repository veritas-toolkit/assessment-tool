----- ----- ----- ----- ----- ----- ----- ----- ----- -----
----------- template_questionnaire
insert into vat_template_questionnaire(
name, type, description, created_time,
part_a_title, part_b_title, part_c_title, part_d_title, part_e_title
)values(
"default", 1, "Basic Questionnaire Template", datetime('now'),
"Describe system objectives and context.",
"Examine Data and Models for Unintentional Bias",
"Measure disadvantage.",
"Justify the use of personal attributes.",
"Examine system monitoring and review.");

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
----- template_question
-- A1
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "A", 1, 0, "What are the business objectives of the system and how is AIDA used to achieve these objectives?",
null, datetime('now'), 1);
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "A", 1, 1, "What are the business objectives of the system?", null, datetime('now'), 1);
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "A", 1, 2, "How is the label defined?", null, datetime('now'), 1);
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "A", 1, 3, "How is the model used to select customers and to achieve business objectives?", null, datetime('now'), 1);

-- A2
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "A", 2, 0, "Who are the individuals and groups that are considered to be at-risk of being systematically disadvantaged by the system?", null, datetime('now'), 1);

-- A3
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "A", 3, 0, "What are potential harms and benefits created by the system’s operation that are relevant to the risk of systematically disadvantaging the individuals and groups in A2?",
    null, datetime('now'), 1);
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "A", 3, 1, "What are the correct decisions and errors that the system can make?", null, datetime('now'), 1);
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "A", 3, 2, "Are there any simplifying assumptions?", null, datetime('now'), 1);

-- A4
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "A", 4, 0, "What are the fairness objectives of the system, with respect to the individuals and groups in A2 and the harms and benefits in A3?",
null, datetime('now'), 1);

-- B1
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "B", 1, 0, "What errors, biases or properties are present in the data used by the system that may impact the system’s fairness?",
    null, datetime('now'), 0);
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "B", 1, 1, "Is there representation bias in the data?", null, datetime('now'), 0);
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "B", 1, 2, "Is there imbalance in the labels?",
    null, datetime('now'), 0);
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "B", 1, 3, "What are the other potential sources of bias in the data?",
    null, datetime('now'), 0);

-- B2
insert into vat_template_question(template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "B", 2, 0, "How are these impacts being mitigated?",
    null, datetime('now'), 0);

-- B3
insert into vat_template_question(template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "B", 3, 0, "How does the system use AIDA models (with, or separately from, business rules and human judgement) to achieve its objectives? ",
    null, datetime('now'), 0);
insert into vat_template_question(template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "B", 3, 1, "Which attributes are included in the model?",
    null, datetime('now'), 0);
insert into vat_template_question(template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "B", 3, 2, "How the model outputs is used to achieve the system’s objectives?",
    null, datetime('now'), 0);

-- B4
insert into vat_template_question(template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "B", 4, 0, "What are the performance estimates of the AIDA models in the system and the uncertainties in those estimates? ",
    null, datetime('now'), 0);
insert into vat_template_question(template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "B", 4, 1, "What are the performance estimates of the AIDA model?", null, datetime('now'), 0);
insert into vat_template_question(template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "B", 4, 2, "What is the confusion matrix for the model?", null, datetime('now'), 0);
insert into vat_template_question(template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "B", 4, 3, "Is the model well-calibrated?", null, datetime('now'), 0);
-- B5
insert into vat_template_question(template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "B", 5, 0, "What are the quantitative estimates of the system’s performance against its business objectives and the uncertainties in those estimates?",
    null, datetime('now'), 0);
insert into vat_template_question(template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "B", 5, 1, "How the quantitative estimates are used to quantify the performance against the business objective?",
    null, datetime('now'), 0);
insert into vat_template_question(template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "B", 5, 2, "What is the relation between the score threshold and primary performance indicator?",
    null, datetime('now'), 0);

-- C1
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "C", 1, 0, "What are the quantitative estimates of the system’s performance against its fairness objectives and the uncertainties in those estimates, assessed over the individuals and groups in A2 and the potential harms and benefits in A3? ",
    null, datetime('now'), 0);
-- C2
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "C", 2, 0, "What are the achievable tradeoffs between the system’s fairness objectives and its other objectives?",
    null, datetime('now'), 0);
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "C", 2, 1, "What is the tradeoff between model performance and fairness?", null, datetime('now'), 0);
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "C", 2, 2, "Are there any graphical or numerical outputs to visualize the tradeoff?", null, datetime('now'), 0);
--C3
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "C", 3, 0, "Why are the fairness outcomes observed in the system preferable to these alternative tradeoffs?", null, datetime('now'), 0);

-- D1
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "D", 1, 0, "What personal attributes are used as part of the operation or assessment of the system?", null, datetime('now'), 0);
-- D2
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "D", 2, 0, "How did the process of identifying personal attributes take into account ethical objectives of the system, and the people identified as being at risk of disadvantage?",
    null, datetime('now'), 0);
-- D3
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "D", 3, 0, "For every personal attribute and potential proxy for a personal attribute, why is its inclusion justified given the system objectives, the data, and the quantified performance and fairness measures? ",
    null, datetime('now'), 0);
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "D", 3, 1, "What method is used to measure the importance of personal attributes?",
    null, datetime('now'), 0);
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "D", 3, 2, "What is the feature importance of the personal attributes?",
    null, datetime('now'), 0);
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "D", 3, 3, "How are the features correlated with each other?",
    null, datetime('now'), 0);

--E1
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "E", 1, 0, "How is the system’s monitoring and review regime designed to detect abnormal operation and unintended harms to individuals or groups?",
    null, datetime('now'), 1);

insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "E", 1, 1, "Is there a shift in the distribution of features?",
    null, datetime('now'), 1);

insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "E", 1, 2, "Is there a shift in the relationship between features and targets?",
    null, datetime('now'), 1);

insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "E", 1, 3, "What method and metric are used to detect data shift?",
    null, datetime('now'), 1);

-- E2
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "E", 2, 0, "How does the system’s monitoring and review regime ensure that the system’s impacts are aligned with its fairness and other objectives (A1 and A4)? ",
    null, datetime('now'), 1);

insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "E", 2, 1, "What are the monitoring and review processes and stages?", null, datetime('now'), 1);

insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "E", 2, 2, "How do these review processes relate to the fairness and other objectives of the system?", null, datetime('now'), 1);

-- E3
insert into vat_template_question( template_id, part, part_serial, sub_serial, content, hint, edit_time, editable)
values(1, "E", 3, 0, "What are the mechanisms for mitigating unintended harms to individuals or groups arising from the system’s operation？",
null, datetime('now'), 1);
