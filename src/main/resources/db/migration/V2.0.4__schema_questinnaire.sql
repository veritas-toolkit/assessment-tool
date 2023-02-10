----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_questionnaire_version (
    vid bigint primary key, -- version id
    project_id integer not null,
    model_artifact_vid integer,
    creator_user_id integer not null,
    created_time varchar(100) not null,
    message varchar(2000),
    exported boolean default false
);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_question_node (
    questionnaire_vid bigint not null,
    project_id integer not null,
    question_id bigint not null,
    question_vid bigint not null,
    main_question_id bigint not null,
    principle varchar(10) not null,
    step integer not null,
    serial_of_principle integer not null,
    sub_serial integer not null,
    primary key (questionnaire_vid, question_id)
);
create index vat2_ix_qsv_pid on vat2_question_node(project_id);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_question_meta (
    id bigint primary key, -- same as the question init version
    project_id integer not null,
    main_question_id bigint not null,
    current_vid bigint not null,
    content_editable boolean not null default false, -- whether the question can be edited.
    answer_required boolean not null default false,
    add_start_questionnaire_vid bigint not null,
    delete_start_questionnaire_vid bigint
);
create index vat2_ix_qm_pid on vat2_question_meta (project_id);
create index vat2_ix_qm_mqid on vat2_question_meta (main_question_id);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_question_version (
    vid integer primary key,
    question_id integer not null,
    main_question_id integer not null,
    project_id integer,
    content clob not null,
    hint varchar(2000),
    content_editable boolean not null default false, -- whether the question can be edited.
    content_edit_user_id integer,
    content_edit_time varchar(100),
    answer clob,
    answer_required boolean not null default false,
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