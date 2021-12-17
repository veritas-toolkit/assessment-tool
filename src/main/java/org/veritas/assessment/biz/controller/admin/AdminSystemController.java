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

package org.veritas.assessment.biz.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.system.service.SystemConfigService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/admin/system")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminSystemController {

    @Autowired
    SystemConfigService systemConfigService;

    @Operation(summary = "Get all default properties for creating user.")
    @GetMapping("user-default-property")
    public Map<String, String> findAllUserDefaultProperties() {
        return systemConfigService.findAllUserDefaultProperties();
    }

    @Operation(summary = "Update default property for creating user.")
    @PostMapping("user-default-property")
    public Map<String, String> updateUserDefaultProperties(@RequestBody Map<String, String> configMap) {
        if (configMap != null && !configMap.isEmpty()) {
            configMap.forEach(systemConfigService::updateConfig);
        }
        return systemConfigService.findAllUserDefaultProperties();
    }

    @Operation(summary = "Get system config property.")
    @GetMapping("/config")
    public Map<String, String> getAllConfigList() {
        return systemConfigService.findAllConfigProperties();
    }

    @Operation(summary = "Update system config property.")
    @PostMapping("/config")
    public void updateConfig(@RequestBody Map<String, String> configMap) {
        if (configMap != null && !configMap.isEmpty()) {
            configMap.forEach(systemConfigService::updateConfig);
        }
    }

    private static final String GIT_VERSION_FILE_PATH = "/git-version.json";
    private static final String versionInfo;

    static {
        String json;
        try (InputStream is = new ClassPathResource(GIT_VERSION_FILE_PATH).getInputStream()) {
            json = IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            log.warn("can not read git version info file[{}].", GIT_VERSION_FILE_PATH,
                    exception);
            json = "{}";
        }
        versionInfo = json;
    }

    @RequestMapping(path = "/version", method = RequestMethod.GET,
            produces = "application/json; charset=UTF-8")
    public String getGitVersion() {
        if (StringUtils.isEmpty(versionInfo)) {
            throw new NotFoundException("Not found version information.");
        }
        return versionInfo;
    }
}
