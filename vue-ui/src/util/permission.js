function permissionCheck(permissionList, target_permission) {
    let permission = target_permission;
    if (permission instanceof PermissionType) {
        permission = target_permission.getName();
    }
    return permissionList.indexOf(permission) !== -1;
}

class PermissionType {
    static PROJECT_CREATE = new PermissionType('project:create');
    static PROJECT_DELETE = new PermissionType('project:delete');
    static PROJECT_EDIT = new PermissionType('project:edit');
    static PROJECT_READ = new PermissionType('project:read');
    static PROJECT_MANAGE_MEMBERS = new PermissionType('project:manage members');
    static PROJECT_EDIT_QUESTIONNAIRE = new PermissionType('project:edit questionnaire');
    static PROJECT_UPLOAD_JSON = new PermissionType('project:upload json');
    static PROJECT_INPUT_ANSWER = new PermissionType('project:input answer');
    static GROUP_CREATE = new PermissionType('group:create');
    static GROUP_DELETE = new PermissionType('group:delete');
    static GROUP_EDIT = new PermissionType('group:"edit"');
    static GROUP_READ = new PermissionType('group:read');
    static GROUP_MANAGE_MEMBERS = new PermissionType('group:manage members');

    constructor(name) {
        this.name = name;
    }

    getName() {
        return this.name;
    }
}

export {permissionCheck, PermissionType}