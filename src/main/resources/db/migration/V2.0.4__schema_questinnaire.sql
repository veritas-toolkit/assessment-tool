----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_questionnaire_version (
    vid integer primary key autoincrement, -- version id
    project_id integer,
    model_artifact_vid integer,
    creator_user_id integer not null,
    created_time varchar(100) not null,
    message varchar(2000)
);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_questionnaire_structure_version (
    questionnaire_vid integer not null,
    project_id integer not null,
    question_id integer not null,
    question_vid integer not null,
    main_question_vid integer not null,
    principle varchar(10) not null,
    serial_no integer not null,
    sub_serial_no integer not null
);
create index vat2_ix_qsv_pid on vat2_questionnaire_structure_version(project_id);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_question (
    id integer primary key autoincrement,
    project_id integer not null,
    main_question_id integer not null,
    editable boolean not null default false, -- The question content can be edited.
    answer_required number(1) not null default 0,
    questionnaire_start_vid integer not null,
    questionnaire_end_vid integer
);
create index vat2_ix_q_pid on vat2_question(project_id);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_question_version (
    vid integer primary key autoincrement,
    question_id integer not null,
    project_id integer,
    content varchar(255) not null,
    hint varchar(255),
    edit_time varchar(100) not null,
    answer clob,
    answer_edit_time varchar(100)
);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_questionnaire_template (
    id integer primary key,
    name varchar(2000) not null,
    description varchar(2000) not null,
    type number(1) not null,                -- 1-system, 2-user defined
    creator_user_id number not null,
    created_time varchar(100) not null
);

create table vat2_template_question (
    id integer primary key autoincrement,
    template_id integer not null,
    main_question_id integer not null,
    principle varchar(10) not null,
    serial_no integer not null,
    sub_serial_no integer not null,
    content varchar(2000) not null,
    hint varchar(255),
    editable number(1) not null,
    editor_user_id integer not null,
    edit_time varchar(100) not null,
    answer_required number(1) not null default 0
);