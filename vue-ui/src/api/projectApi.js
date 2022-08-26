import request from "@/api/request";
import Router from '@/router'
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
    },
    downloadJsonFile(projectId) {
        return request({
            url: `/api/project/${projectId}/modelArtifact/download`,
            method: 'get',
            responseType: "blob",
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        })
    },
    downloadHistoryJsonFile(projectId,versionId) {
        return request({
            url: `/api/project/${projectId}/history/${versionId}/modelArtifact/download`,
            method: 'get',
            responseType: "blob",
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        })
    },
    previewPdf(projectId) {
        return request({
            url:`/api/project/${projectId}/report/preview_pdf`,
            method: 'get',
            responseType: "blob",
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        })
    },
    previewHistoryPdf(projectId,versionId) {
        return request({
            url:`/api/project/${projectId}/history/${versionId}/report`,
            method: 'get',
            responseType: "blob",
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        })
    },
    async questionnaireHistory(projectId,versionId) {
        await Router.push({path:'/assessmentToolHistory',query: {projectId:projectId,versionId:versionId}})
    },
};

export default projectApi;