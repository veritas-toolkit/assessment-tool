create table vat_project_report (
    version_id integer primary key autoincrement,
    project_id integer not null,
    version_id_of_project integer not null,
    creator_user_id integer not null,
    created_time varchar(100) not null,
    tag varchar(255),
    version varchar(255),
    message varchar(2000),
    model_artifact_version_id integer not null,
    questionnaire_version_id integer not null,
    pdf_path varchar(2000)
);

create index vat_project_r_v_pid on vat_project_report(project_id, version_id_of_project);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat_model_artifact (
    project_id integer primary key,
    upload_user_id integer not null,
    upload_time varchar(100) not null,
    filename varchar(1000),
    json_zip_path varchar(1000),
    json_content_sha256 varchar(1000)
);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat_model_artifact_version (
    version_id integer primary key autoincrement,
    project_id integer not null,
    upload_user_id integer not null,
    upload_time varchar(100) not null,
    filename varchar(1000),
    json_zip_path varchar(1000),
    json_content_sha256 varchar(1000)
);

create index vat_model_artifact_v_pid on vat_model_artifact(project_id);


