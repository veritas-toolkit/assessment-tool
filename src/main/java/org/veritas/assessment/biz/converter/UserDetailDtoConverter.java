package org.veritas.assessment.biz.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.veritas.assessment.biz.dto.UserDetailDto;
import org.veritas.assessment.system.entity.User;

@Component
@Slf4j
public class UserDetailDtoConverter implements Converter<UserDetailDto, User> {
    @Override
    public UserDetailDto convertFrom(User user) {
        UserDetailDto dto = new UserDetailDto();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
