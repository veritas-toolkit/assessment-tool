----- ----- ----- ----- ----- ----- ----- ----- ----- -----
-- vat2_permission
insert into vat2_permission(id, resource_type, name, description)
values(101, 'project', 'create', 'create project.');
insert into vat2_permission(id, resource_type, name, description)
values(102, 'project', 'delete', 'delete project.');
insert into vat2_permission(id, resource_type, name, description)
values(103, 'project', 'edit', 'edit basic information of the project.');
insert into vat2_permission(id, resource_type, name, description)
values(104, 'project', 'read', 'view project detail.');
insert into vat2_permission(id, resource_type, name, description)
values(121, 'project', 'manage members', 'Input the answers of questionnaire to the project.');
insert into vat2_permission(id, resource_type, name, description)
values(122, 'project', 'edit questionnaire', 'Edit the questions of the project questionnaire.');
insert into vat2_permission(id, resource_type, name, description)
values(141, 'project', 'upload json', 'Upload artifact json file to the project.');
insert into vat2_permission(id, resource_type, name, description)
values(142, 'project', 'input answer', 'Input the answers of questionnaire to the project.');


insert into vat2_permission(id, resource_type, name, description)
values(301, 'group', 'create', 'create group.');
insert into vat2_permission(id, resource_type, name, description)
values(302, 'group', 'delete', 'delete group.');
insert into vat2_permission(id, resource_type, name, description)
values(303, 'group', 'edit', 'edit group detail.');
insert into vat2_permission(id, resource_type, name, description)
values(304, 'group', 'read', 'view group detail.');
insert into vat2_permission(id, resource_type, name, description)
values(305, 'group', 'manage members', 'add a member into the group.');

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
-- vat2_role
insert into vat2_role(id, type, name, description)
values(101, 'project', 'owner', 'Owner of the project.');
insert into vat2_role(id, type, name, description)
values(102, 'project', 'developer', 'Developer of the project.');
insert into vat2_role(id, type, name, description)
values(103, 'project', 'assessor', 'Only can view the project, cannot modify artifact model and questionnaire.');

insert into vat2_role(id, type, name, description)
values(301, 'group', 'owner', 'Owner of the group.');
insert into vat2_role(id, type, name, description)
values(302, 'group', 'developer', 'Developer of the group. Maintainer cannot delete the group.');
insert into vat2_role(id, type, name, description)
values(303, 'group', 'assessor', 'Only can view the group and related projects, cannot modify artifact model and questionnaire.');

----- ----- ----- ----- ----- ----- ----- ----- ----- -----
-- vat2_role_permission
insert into vat2_role_permission(role_id, permission_id)
values(101, 102);
insert into vat2_role_permission(role_id, permission_id)
values(101, 103);
insert into vat2_role_permission(role_id, permission_id)
values(101, 104);
insert into vat2_role_permission(role_id, permission_id)
values(101, 121);
insert into vat2_role_permission(role_id, permission_id)
values(101, 122);
insert into vat2_role_permission(role_id, permission_id)
values(101, 141);
insert into vat2_role_permission(role_id, permission_id)
values(101, 142);

insert into vat2_role_permission(role_id, permission_id)
values(102, 103);
insert into vat2_role_permission(role_id, permission_id)
values(102, 104);
insert into vat2_role_permission(role_id, permission_id)
values(102, 141);
insert into vat2_role_permission(role_id, permission_id)
values(102, 142);

insert into vat2_role_permission(role_id, permission_id)
values(103, 104);

insert into vat2_role_permission(role_id, permission_id)
values(301, 101);
insert into vat2_role_permission(role_id, permission_id)
values(301, 102);
insert into vat2_role_permission(role_id, permission_id)
values(301, 103);
insert into vat2_role_permission(role_id, permission_id)
values(301, 104);
insert into vat2_role_permission(role_id, permission_id)
values(301, 121);
insert into vat2_role_permission(role_id, permission_id)
values(301, 141);
insert into vat2_role_permission(role_id, permission_id)
values(301, 142);
insert into vat2_role_permission(role_id, permission_id)
values(301, 301);
insert into vat2_role_permission(role_id, permission_id)
values(301, 302);
insert into vat2_role_permission(role_id, permission_id)
values(301, 303);
insert into vat2_role_permission(role_id, permission_id)
values(301, 304);
insert into vat2_role_permission(role_id, permission_id)
values(301, 305);

insert into vat2_role_permission(role_id, permission_id)
values(302, 103);
insert into vat2_role_permission(role_id, permission_id)
values(302, 104);
insert into vat2_role_permission(role_id, permission_id)
values(302, 141);
insert into vat2_role_permission(role_id, permission_id)
values(302, 142);
insert into vat2_role_permission(role_id, permission_id)
values(302, 301);
insert into vat2_role_permission(role_id, permission_id)
values(302, 304);

insert into vat2_role_permission(role_id, permission_id)
values(303, 104);
insert into vat2_role_permission(role_id, permission_id)
values(303, 304);