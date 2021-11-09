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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.veritas.assessment.system.auth.CustomAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.logout()
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                        response.setStatus(HttpStatus.OK.value());
                    }
                });
        http.antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").hasRole("ADMIN")
                .antMatchers("/login").permitAll()
                .antMatchers("/account/change_password").permitAll()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/static/**").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/register").permitAll()
                .antMatchers(HttpMethod.GET, "/api/system/config/register-supported").permitAll()
                .anyRequest().authenticated()
//                .and().formLogin().failureHandler(new ExceptionMappingAuthenticationFailureHandler())
                .and()
                .csrf().disable();
        http.addFilterAt(customAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling()
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint());
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder());
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    CustomAuthenticationFilter customAuthenticationFilter(AuthenticationManager authenticationManager)
            throws Exception {
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter(authenticationManager);
        return filter;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
