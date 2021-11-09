----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat_project_question_comment (
    id integer primary key autoincrement,
    question_id integer not null,
    project_id integer not null,
    user_id integer not null,
    comment varchar(2000),
    created_time varchar(100) not null,
    refer_comment_id integer
);

create index vat_ix_project_q_comment_q on vat_project_question_comment(question_id);
create index vat_ix_project_q_comment_p on vat_project_question_comment(project_id);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat_question_comment_read_log (
    user_id integer not null,
    project_id integer not null,
    question_id integer not null,
    latest_read_comment_id integer not null,
    primary key (user_id, project_id, question_id)
);
