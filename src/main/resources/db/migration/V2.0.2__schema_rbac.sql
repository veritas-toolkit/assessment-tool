----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_permission (
    id integer primary key,
    resource_type varchar(100) not null,
    name varchar(100) not null,
    description varchar(2000)
);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_role (
    id integer primary key,
    type varchar(100) not null, -- 'project' or 'group'
    name varchar(100) not null,
    description varchar(2000)
);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_role_permission (
    role_id integer not null,
    permission_id integer not null
);
create unique index vat2_ix_role_permission on vat2_role_permission(role_id, permission_id);

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
create table vat2_user_role (
    user_id integer not null,
    resource_type integer not null,
    resource_id integer not null,
    role_id integer not null,
    created_time varchar(100) not null,
    expiration_date varchar(100)
);
create unique index vat2_ix_ur_u on vat2_user_role(user_id, resource_type, resource_id, role_id);
create index vat2_ix_ur_resource on vat2_user_role(resource_id, resource_type);