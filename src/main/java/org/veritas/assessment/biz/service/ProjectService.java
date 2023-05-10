/*
 * Copyright 2021 MAS Veritas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.veritas.assessment.biz.service;

import org.veritas.assessment.biz.action.QueryProjectPageableAction;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.constant.RoleType;
import org.veritas.assessment.system.dto.MembershipDto;
import org.veritas.assessment.system.entity.Member;
import org.veritas.assessment.system.entity.User;

import java.util.ArrayList;
import java.util.List;

public interface ProjectService {
    Project createProject(User operator, Project project, TemplateQuestionnaire templateQuestionnaire);

    Project createProject(User operator, Project project, Project copyFromProject);

    int delete(Integer projectId);
    int archive(Integer projectId);
    int unarchive(Integer projectId);

    int deleteProjectByUserOwner(Integer ownerId);

    int deleteProjectByGroupOwner(Integer ownerId);

    Project findProjectById(int projectId);

    Project modifyProject(Project project);

    Member addMember(Integer projectId, Integer userId, RoleType roleType);

    default Member addMember(Integer projectId, MembershipDto membershipDto) {
        return addMember(projectId, membershipDto.getUserId(), membershipDto.getType());
    }

    default List<Member> addMemberList(Integer projectId, List<MembershipDto> dtoList) {
        List<Member> memberList = new ArrayList<>(dtoList.size());
        dtoList.forEach(dto -> {
            memberList.add(addMember(projectId, dto));
        });
        return memberList;
    }

    int removeMember(int projectId, int userId);

    Member modifyMember(Integer projectId, Integer userId, RoleType roleType);


    List<Member> findMemberList(int projectId);

    Pageable<Project> findProjectPageable(User operator, QueryProjectPageableAction queryAction);
    Pageable<Project> findProjectPageable(Integer userId, String prefix, String keyword, int page, int pageSize);

    List<Project> findProjectList(Integer userId);

    Pageable<Project> findProjectPageableByCreator(User user, String prefix, String keyword, int page, int pageSize);


    List<Project> findByUserOwnerId(Integer userId);

    List<Project> findByGroupOwnerId(Integer groupId);

    Pageable<Project> findProjectPageableOfGroup(Integer groupId, String prefix, String keyword, int page, int pageSize);

    long countProjectOfGroup(Integer groupId);

    void updateModelArtifact(User operator, Integer projectId, ModelArtifact modelArtifact);


}
