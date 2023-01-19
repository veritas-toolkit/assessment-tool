----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_questionnaire_version (
    vid integer primary key autoincrement, -- version id
    project_id integer not null,
    model_artifact_vid integer,
    creator_user_id integer not null,
    created_time varchar(100) not null,
    message varchar(2000)
);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_questionnaire_version_structure (
    questionnaire_vid integer not null,
    project_id integer not null,
    question_id integer not null,
    question_vid integer not null,
    main_question_vid integer not null,
    principle varchar(10) not null,
    step integer not null,
    serial_of_principle integer not null,
    sub_serial integer not null
);
create index vat2_ix_qsv_pid on vat2_questionnaire_version_structure(project_id);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_question_meta (
    id integer primary key autoincrement,
    current_vid integer,
    project_id integer not null,
    main_question_id integer not null,
    editable boolean not null default false, -- The questionMeta content can be edited.
    answer_required number(1) not null default 0,
    add_start_questionnaire_vid integer not null,
    delete_start_questionnaire_vid integer
);
create index vat2_ix_qm_pid on vat2_question_meta (project_id);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_question_version (
    vid integer primary key autoincrement,
    question_id integer not null,
    main_question_id integer not null,
    project_id integer,
    content clob not null,
    hint varchar(2000),
    content_edit_user_id integer,
    content_edit_time varchar(100),
    answer clob,
    answer_edit_user_id integer,
    answer_edit_time varchar(100)
);
create index vat_qv_pid on vat2_question_version(project_id);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_template_questionnaire (
    id integer primary key,
    name varchar(2000) not null,
    description varchar(2000) not null,
    business_scenario integer not null,
    type number(1) not null,                -- 1-system, 2-user defined
    creator_user_id number not null,
    created_time varchar(100) not null
);

create table vat2_template_question (
    id integer primary key autoincrement,
    template_id integer not null,
    principle varchar(10) not null,
    step integer not null,
    serial_of_principle integer not null,
    sub_serial integer not null,
    content varchar(2000) not null,
    hint varchar(255),
    editable number(1) not null default false,
    editor_user_id integer not null,
    edit_time varchar(100) not null,
    answer_required number(1) not null default false,
    deleted number(1) not null default false
);

create index vat2_ix_tq_ti on vat2_template_question(template_id);