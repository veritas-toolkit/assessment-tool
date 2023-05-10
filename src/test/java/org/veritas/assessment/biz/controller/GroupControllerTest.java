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

package org.veritas.assessment.biz.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.dto.GroupDetailDto;
import org.veritas.assessment.biz.dto.GroupDto;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.constant.RoleType;
import org.veritas.assessment.system.dto.GroupCreateDto;
import org.veritas.assessment.system.dto.MembershipDto;
import org.veritas.assessment.system.entity.Group;
import org.veritas.assessment.system.entity.Member;
import org.veritas.assessment.system.service.GroupService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Sql("/sql/unit_test_user.sql")
class GroupControllerTest {
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    private GroupDto createGroup(String name) throws Exception {
        GroupCreateDto dto = new GroupCreateDto();
        dto.setName(name);
        dto.setDescription("new group description.");
        MvcResult mvcResult = mockMvc.perform(put("/api/group/new")
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        GroupDto groupDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), GroupDto.class);
        log.info("group: {}", dto);
        return groupDto;
    }

    @Test
    void testListPageable() throws Exception {
        int hashCode = hashCode();
        int count = 10;
        for (int i = 1; i <= count; i++) {
            createGroup("name_" + hashCode + "_" + i);
        }
        MvcResult mvcResult = mockMvc.perform(get("/api/group")
                        .with(user("1").roles("ADMIN", "USER"))
                        .param("page", "1")
                        .param("pageSize", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        Pageable<GroupDto> pageable = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<Pageable<GroupDto>>() {
                });
        assertEquals(count, pageable.getTotal());
        assertEquals(count, pageable.getRecords().size());
    }

    @Test
    void testListAll() throws Exception {
        int hashCode = hashCode();
        int count = 10;
        for (int i = 1; i <= count; i++) {
            createGroup("name_" + hashCode + "_" + i);
        }
        MvcResult mvcResult = mockMvc.perform(get("/api/group/all")
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        List<GroupDto> list = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<GroupDto>>() {
                });
        assertEquals(count, list.size());
    }

    @Test
    void testNew_success() throws Exception {
        GroupCreateDto dto = new GroupCreateDto();
        dto.setName("group_name");
        dto.setDescription("new group description.");
        MvcResult mvcResult = mockMvc.perform(put("/api/group/new")
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        GroupDto groupDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), GroupDto.class);
        log.info("group: {}", groupDto);
    }

    @Test
    void testModify_success() throws Exception {
        GroupDto groupDto = createGroup("for modify");

        Group group = new Group();
        group.setName("--new name");
        group.setDescription("description----xxx");

        MvcResult mvcResult = mockMvc.perform(post("/api/group/{groupId}", groupDto.getId())
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(group)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        GroupDto dto2 = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                GroupDto.class);
        log.info("group dto:\n{}",
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto2));
        assertEquals(group.getName(), dto2.getName());
        assertEquals(group.getDescription(), dto2.getDescription());
    }

    @Test
    void testDetail_success() throws Exception {
        GroupDto groupDto = createGroup("for detail");

        MvcResult mvcResult = mockMvc.perform(get("/api/group/{groupId}/detail", groupDto.getId())
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        GroupDetailDto groupDetailDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                GroupDetailDto.class);
        log.info("group detail dto:\n{}",
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(groupDetailDto));
    }

    @Test
    void testAddMembers_success() throws Exception {
        GroupDto groupDto = createGroup("for_add_member");
        List<MembershipDto> dtoList = new ArrayList<>();
        for (int id = 2; id <= 3; id++) {
            MembershipDto membershipDto = new MembershipDto();
            membershipDto.setUserId(id);
            membershipDto.setType(RoleType.DEVELOPER);
            dtoList.add(membershipDto);
        }

        MvcResult mvcResult = mockMvc.perform(put("/api/group/{groupId}/member", groupDto.getId())
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoList)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        List<Member> list = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<Member>>() {
                });
        log.info("member list:\n{}",
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(list));
        assertEquals(dtoList.size(), list.size());
    }


    @Autowired
    private GroupService groupService;

    @Test
    void testDeleteMember_success() throws Exception {
        GroupDto groupDto = createGroup("for_delete_member");
        List<MembershipDto> dtoList = new ArrayList<>();
        for (int id = 2; id <= 3; id++) {
            MembershipDto membershipDto = new MembershipDto();
            membershipDto.setUserId(id);
            membershipDto.setType(RoleType.DEVELOPER);
            dtoList.add(membershipDto);
        }
        List<Member> memberList = addMember(groupDto.getId(), dtoList);
        assertEquals(2, memberList.size());
        assertEquals(3, groupService.getMemberList(groupDto.getId()).size());

        MvcResult mvcResult = mockMvc.perform(delete(
                        "/api/group/{groupId}/member/{userId}", groupDto.getId(), 2)
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        assertEquals(2, groupService.getMemberList(groupDto.getId()).size());
    }


    @Test
    void testModifyMember_success() throws Exception {
        GroupDto groupDto = createGroup("for_modify_member");
        List<MembershipDto> dtoList = new ArrayList<>();
        for (int id = 2; id <= 3; id++) {
            MembershipDto membershipDto = new MembershipDto();
            membershipDto.setUserId(id);
            membershipDto.setType(RoleType.DEVELOPER);
            dtoList.add(membershipDto);
        }
        List<Member> memberList = addMember(groupDto.getId(), dtoList);
        assertEquals(2, memberList.size());
        assertEquals(1, memberList.stream()
                .filter(member -> member.getUserId() == 2 && member.getType() == RoleType.DEVELOPER)
                .count());


        MembershipDto membershipDto = new MembershipDto();
        membershipDto.setUserId(2);
        membershipDto.setType(RoleType.OWNER);

        MvcResult mvcResult = mockMvc.perform(post(
                        "/api/group/{groupId}/member", groupDto.getId())
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(membershipDto)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        Member member = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Member.class);
        assertNotNull(member);
        assertEquals(RoleType.OWNER, member.getType());
    }


    private List<Member> addMember(int groupId, List<MembershipDto> dtoList) throws Exception {
        MvcResult mvcResult = mockMvc.perform(put("/api/group/{groupId}/member", groupId)
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoList)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        List<Member> list = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<Member>>() {
                });

        return list;
    }
}