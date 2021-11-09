create table vat_user (
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
CREATE UNIQUE INDEX vat_ix_user_username ON vat_user (username, delete_time);
CREATE UNIQUE INDEX vat_ix_user_email ON vat_user (email, delete_time);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----

create table vat_group (
    id integer primary key autoincrement,
    name varchar(255) not null,
    description varchar(2000),
    creator_user_id integer not null,
    created_time varchar(100) not null,
    last_modified_time varchar(100) not null,
    deleted number(1) not null default 0,
    delete_time varchar(100) not null default 'null'
);
CREATE UNIQUE INDEX vat_ix_group_name ON vat_group (name, delete_time);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat_business_scenario (
    code integer primary key,
    name varchar(255) not null,
    description varchar(2000)
);


----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat_project (
    id integer primary key autoincrement,
    name varchar(255) not null,
    description varchar(2000),
    user_owner_id integer,      -- one and only one of user_owner_id and group_owner_id must be not null.
    group_owner_id integer,
    owner_type generated always as (case when group_owner_id is not null then 'g' else 'u'  end) stored,
    owner_id generated always as (case when group_owner_id is not null then group_owner_id else user_owner_id  end) stored,
    business_scenario integer not null, -- may refer to another table
    model_artifact_id integer,
    creator_user_id integer not null,
    created_time varchar(100) not null,
    last_edited_time varchar(100),
    deleted number(1) not null default 0,
    delete_time varchar(100) not null default 'null'
);
--CREATE UNIQUE INDEX vat_ix_project_nug ON vat_project (name, user_owner_id, group_owner_id);
CREATE UNIQUE INDEX vat_ix_project_nug ON vat_project (name, owner_id, owner_type, delete_time);
CREATE INDEX vat_ix_project_un ON vat_project (user_owner_id, name);
CREATE INDEX vat_ix_project_gn ON vat_project (group_owner_id, name);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat_permission (
    id integer primary key,
    resource_type varchar(100) not null,
    name varchar(100) not null,
    description varchar(2000)
);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat_role (
    id integer primary key,
    type varchar(100) not null, -- 'project' or 'group'
    name varchar(100) not null,
    description varchar(2000)
);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat_role_permission (
    role_id integer not null,
    permission_id integer not null
);
create unique index vat_ix_role_permission on vat_role_permission(role_id, permission_id);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat_user_role (
    user_id integer not null,
    resource_type integer not null,
    resource_id integer not null,
    role_id integer not null,
    created_time varchar(100) not null,
    expiration_date varchar(100)
);
create unique index vat_ix_ur_u on vat_user_role(user_id, resource_type, resource_id, role_id);
create index vat_ix_ur_resource on vat_user_role(resource_id, resource_type);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----