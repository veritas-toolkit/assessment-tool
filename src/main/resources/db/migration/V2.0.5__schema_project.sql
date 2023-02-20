create table vat2_project_report (
    version_id integer primary key autoincrement,
    project_id integer not null,
    version_id_of_project integer not null,
    creator_user_id integer not null,
    created_time varchar(100) not null,
    tag varchar(255),
    version varchar(255), -- x.y.z, created by user
    message varchar(2000),
    model_artifact_vid integer not null,
    questionnaire_vid integer not null,
    pdf_path varchar(2000)
);
create index vat2_ix_project_r_pid on vat2_project_report(project_id);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_model_artifact (
    version_id integer primary key autoincrement,
    project_id integer not null,
    upload_user_id integer not null,
    upload_time varchar(100) not null,
    filename varchar(1000) not null,
    json_zip_path varchar(1000) not null,
    json_content_sha256 varchar(1000) not null
);

create index vat2_ix_model_v_pid on vat2_model_artifact(project_id);


----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_question_comment (
    id integer primary key autoincrement,
    question_id integer not null,
    main_question_id integer not null,
    project_id integer not null,
    user_id integer not null,
    comment varchar(2000),
    created_time varchar(100) not null,
    refer_comment_id integer
);

create index vat2_ix_project_q_comment_q on vat2_question_comment(question_id);
create index vat2_ix_project_q_comment_p on vat2_question_comment(project_id);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_question_comment_read_log (
    user_id integer not null,
    project_id integer not null,
    question_id integer not null,
    latest_read_comment_id integer not null,
    primary key (user_id, project_id, question_id)
);