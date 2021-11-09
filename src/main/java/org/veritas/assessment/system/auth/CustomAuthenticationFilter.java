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

package org.veritas.assessment.system.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.veritas.assessment.system.config.ControllerExceptionHandler;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    public static final String USERNAME_KEY = "username";
    public static final String PASSWORD_KEY = "password";
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher("/api/login", "POST");
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    @PostConstruct
    public void init() {
        super.setAuthenticationSuccessHandler((request, response, authentication) -> {
            String username = (String) request.getAttribute(USERNAME_KEY);
            userService.successLogin(username);
            User user = userService.findUserByUsernameOrEmail(username);
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().append(objectMapper.writeValueAsString(user));
        });

        super.setAuthenticationFailureHandler((request, response, exception) -> {
            String username = (String) request.getAttribute(USERNAME_KEY);
            if (StringUtils.isNotEmpty(username)) {
                userService.failLogin(username);
            }
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ControllerExceptionHandler.ErrorMessage message =
                    ControllerExceptionHandler.errorMessage(HttpStatus.UNAUTHORIZED, exception.getMessage());
            response.getWriter().append(objectMapper.writeValueAsString(message));
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String contentType = request.getContentType();
        if (!StringUtils.equalsAny(contentType,
                MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE)) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        ObjectMapper mapper = new ObjectMapper();
        UsernamePasswordAuthenticationToken token = null;
        String username = null;
        String password = null;
        try (InputStream is = request.getInputStream()) {
            @SuppressWarnings("unchecked")
            Map<String, String> authenticationBean = mapper.readValue(is, Map.class);
            username = authenticationBean.get(USERNAME_KEY);
            password = authenticationBean.get(PASSWORD_KEY);
        } catch (IOException e) {
            log.error("Parse request fail.", e);
            throw new AuthenticationServiceException("Can not parse authentication content.");
        }
        username = username != null ? username : "";
        password = password != null ? password : "";
        request.setAttribute(USERNAME_KEY, username);
        token = new UsernamePasswordAuthenticationToken(username, password);
        token.setDetails(this.authenticationDetailsSource.buildDetails(request));
        return this.getAuthenticationManager().authenticate(token);
    }
}
