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

package org.veritas.assessment.biz.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.veritas.assessment.biz.dto.GroupDto;
import org.veritas.assessment.system.entity.Group;
import org.veritas.assessment.system.service.GroupService;
import org.veritas.assessment.system.service.UserService;

import java.util.Objects;

@Component
@Slf4j
public class GroupDtoConverter implements Converter<GroupDto, Group> {

    @Override
    public GroupDto convertFrom(Group group) {
        Objects.requireNonNull(group, "The arg[group] cannot be null.");
        GroupDto groupDto = new GroupDto();
        BeanUtils.copyProperties(group, groupDto);
        return groupDto;
    }
}
