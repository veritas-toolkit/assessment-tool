import request from "@/api/request";
// import Vue from "vue";
// import axios from "axios"

// const request = Vue.prototype.$http.create({    timeout: 5000})

const projectApi = {
    getMemberList(projectId) {
        return request({
            url: `api/project/${projectId}/member`,
            method: 'get'
        });
    },

    addMemberList(projectId, memberList) {
        return request({
            url: `api/project/${projectId}/member`,
            method: 'put',
            data: memberList
        })
    },
    deleteMember(projectId, userId) {
        return request({
            url: `api/project/${projectId}/member/${userId}`,
            method: 'delete',
        })
    },

    changeMemberRole(projectId, userId, roleType) {
        return request({
            url: `api/project/${projectId}/member`,
            method: 'post',
            data: {
                'userId': userId,
                'type': roleType
            }
        })
    }
};

export default projectApi;