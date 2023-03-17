import request from "@/api/request";
import Router from '@/router'
import {pl, tr} from "timeago.js/lib/lang";


const projectApi = {
    create(projectCreateDto) {
        return request({
            url: `api/project/new`,
            method: 'post',
            headers: {'Content-Type': 'application/json; charset=UTF-8'},
            data: {
                ...projectCreateDto
            }
        })

    },
    detail(projectId) {
        return request({
            url: `api/project/${projectId}/detail`,
            method: 'get'
        });
    },
    async fetchAllByKeyword(keyword) {
        let page = 1;
        let pageCount = 2;
        let projectList = [];
        let archived = false;
        while (page < pageCount) {
            await request({
                url: `api/project`,
                method: 'get',
                params: {
                    keyword: keyword,
                    archived: archived,
                    page: page,
                    pageSize: 100,
                }
            }).then(response => {
                console.log(response.data)
                projectList.push(...response.data.records);
                page = response.data.page;
                pageCount = response.data.pageCount;
                if (page >= pageCount) {
                    if (archived === false) {
                        page = 1;
                        pageCount = 2;
                        archived = true;
                    }

                }
            });
        }
        return projectList;
    },
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

    fetchHistoryJsonInfo(projectId, versionId) {
        return request({
            url: `/api/project/${projectId}/history/${versionId}/modelArtifact`,
            method: 'get',
            responseType: "json",
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

    fetchSuggestionVersion(projectId) {
        return request({
            url:`/api/project/${projectId}/report/suggestion-version`,
            method: 'get',
            responseType: "json",
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        })
    },

    exportReport(projectId, exportReport) {
        return request({
            url:`/api/project/${projectId}/report/export2`,
            method: 'post',
            responseType: "json",
            headers: {'Content-Type': 'application/json; charset=UTF-8'},
            data: exportReport
        })
    },

    fetchReportHistoryList(projectId) {
        return request({
            url:`/api/project/${projectId}/report/list`,
            method: 'get',
            responseType: "json",
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        })
    },

    fetchReportPdf(projectId, versionId) {
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

    getReportList(projectId) {
        return request({
            url: `/api/project/${projectId}/report/list`,
            method: 'get',
            responseType: "json",
        })
    }

};

export default projectApi;