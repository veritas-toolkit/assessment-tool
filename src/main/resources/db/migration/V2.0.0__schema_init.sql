create table vat2_user (
    id integer primary key autoincrement,
    username varchar(255) not null, -- for login
    full_name varchar(255) not null, -- just for description
    email varchar(255), -- also for login
    password varchar(255) not null,
    admin number(1) not null default 0,
    login_attempt integer not null default 0,
    project_limited integer not null,
    group_limited integer not null,
    created_time varchar(100),
    last_login_time varchar(100),
    locked number(1) not null default 0,
    should_change_password number(1) not null default 1,
    deleted number(1) not null default 0,
    delete_time varchar(100) not null default 'null'
);
----- ----- ----- ----- ----- ----- ----- ----- ----- -----

CREATE UNIQUE INDEX vat2_ix_user_username ON vat2_user (username, delete_time);
CREATE UNIQUE INDEX vat2_ix_user_email ON vat2_user (email, delete_time);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----

create table vat2_group (
    id integer primary key autoincrement,
    name varchar(255) not null,
    description varchar(2000),
    creator_user_id integer not null,
    created_time varchar(100) not null,
    last_modified_time varchar(100) not null,
    deleted number(1) not null default 0,
    delete_time varchar(100) not null default 'null'
);
CREATE UNIQUE INDEX vat2_ix_group_name ON vat2_group (name, delete_time);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_business_scenario (
    code integer primary key,
    name varchar(255) not null,
    description varchar(2000)
);


----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_project (
    id integer primary key autoincrement,
    name varchar(255) not null,
    description varchar(2000),
    user_owner_id integer,      -- one and only one of user_owner_id and group_owner_id must be not null.
    group_owner_id integer,
    owner_type generated always as (case when group_owner_id is not null then 'g' else 'u'  end) stored,
    owner_id generated always as (coalesce(group_owner_id, user_owner_id)) stored,
    business_scenario integer not null, --  refer to business_scenario table
    principle_g boolean not null default true,
    principle_f boolean not null default false,
    principle_ea boolean not null default false,
    principle_t boolean not null default false,
    current_model_artifact_vid integer,
    current_questionnaire_vid integer,

    creator_user_id integer not null,
    created_time varchar(100) not null,
    last_edited_time varchar(100),
    archived boolean not null default 0,
    deleted boolean not null default 0,
    delete_time varchar(100) not null default 'null'
);

CREATE UNIQUE INDEX vat2_ix_project_nug ON vat2_project (name, owner_id, owner_type, delete_time);
CREATE INDEX vat2_ix_project_un ON vat2_project (user_owner_id, name);
CREATE INDEX vat2_ix_project_gn ON vat2_project (group_owner_id, name);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_system_config_entry (
    key varchar(200) primary key,
    value varchar(2000)
);