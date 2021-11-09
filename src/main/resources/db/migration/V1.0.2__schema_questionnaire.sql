----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat_project_questionnaire (
    project_id integer primary key,
    part_a_title varchar(2000) not null,
    part_b_title varchar(2000) not null,
    part_c_title varchar(2000) not null,
    part_d_title varchar(2000) not null,
    part_e_title varchar(2000) not null
);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat_project_question (
    id integer primary key autoincrement,
    project_id integer not null,
    part varchar(255) not null,
    part_serial integer not null,
    sub_serial integer not null,
    content varchar(255) not null,
    hint varchar(255),
    edit_time varchar(100) not null,
    editable number(1) not null,
    answer_required number(1) not null default 0,
    answer clob,
    answer_edit_time varchar(100)
);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat_project_version_questionnaire (
    version_id integer primary key autoincrement,
    project_id integer not null,
    created_time varchar(100) not null,
    part_a_title varchar(2000) not null,
    part_b_title varchar(2000) not null,
    part_c_title varchar(2000) not null,
    part_d_title varchar(2000) not null,
    part_e_title varchar(2000) not null
);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat_project_version_question (
    id integer primary key autoincrement,
    questionnaire_version_id integer not null,
    project_id integer not null,
    question_id integer not null,
    part varchar(255) not null,
    part_serial integer not null,
    sub_serial integer not null,
    content varchar(255) not null,
    hint varchar(255),
    edit_time varchar(100) not null,
    editable number(1) not null,
    answer_required number(1),
    answer clob,
    answer_edit_time varchar(100)
);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat_template_questionnaire (
    template_id integer primary key autoincrement,
    name varchar(2000) not null,
    description varchar(2000) not null,
    type number(1) not null,                        -- 1-system, 2-user defined
    created_time varchar(100) not null,
    part_a_title varchar(2000) not null,
    part_b_title varchar(2000) not null,
    part_c_title varchar(2000) not null,
    part_d_title varchar(2000) not null,
    part_e_title varchar(2000) not null
);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat_template_question (
    id integer primary key autoincrement,
    template_id integer,
    part varchar(255) not null,
    part_serial integer not null,
    sub_serial integer not null,
    content varchar(255) not null,
    hint varchar(255),
    edit_time varchar(100) not null,
    editable number(1) not null,
    answer_required number(1) not null default 0,
    answer clob,
    answer_edit_time varchar(100)
);