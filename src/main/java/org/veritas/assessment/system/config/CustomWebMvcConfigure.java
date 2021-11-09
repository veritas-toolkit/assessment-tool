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

package org.veritas.assessment.system.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.veritas.assessment.system.service.UserService;

import java.util.List;

@Component
@Slf4j
public class CustomWebMvcConfigure implements WebMvcConfigurer {

    @Autowired
    @Lazy
    private UserService userService;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

        resolvers.add(new UserHandlerMethodArgumentResolver(userService));
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configure) {
        log.info("Add url prefix");
//        configure.addPathPrefix("/api", HandlerTypePredicate.forAnnotation(RestController.class));
        configure.addPathPrefix(
                "/api",
                HandlerTypePredicate.forBasePackage("org.veritas.assessment.biz.controller"));
    }
}
