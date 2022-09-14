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

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "veritas")
@Slf4j
public class VeritasProperties {

    /**
     *
     */
    private int maxFailLoginAttempt = 5;
    private int defaultGroupLimited = 10;
    private int defaultProjectLimited = 100;

    private int defaultPageSize = 20;

    /**
     *
     */
    private boolean autoGenerateAnswerStrict = false;

    private String filePath = "file/project";

    private String pythonCommand = "python";

    @PostConstruct
    public void init() throws Exception {
        File dir = new File(filePath);
        if (!dir.exists()) {
            try {
                FileUtils.createParentDirectories(dir);
                boolean created = dir.mkdirs();
                if (!created) {
                    throw new Exception(String.format("Create directory:'%s' failed.", filePath));
                }
            } catch (IOException exception) {
                throw new Exception(String.format("Create directory:'%s' failed.", filePath), exception);
            }
        }


        pythonCommandTest();
    }

    private void pythonCommandTest() throws Exception {
        ProcessBuilder builder = new ProcessBuilder(
                this.pythonCommand,
                "-V");
        builder.redirectErrorStream(false);
        Process process = builder.start();
        String result = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
        log.info("stdout: {}", result);
        String stderr = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
        log.warn("stderr: {}", stderr);

        boolean isPython3 = StringUtils.startsWith(result, "Python 3");
        if (!isPython3) {
            throw new RuntimeException(String.format("command '%s' is not python version 3", this.pythonCommand));
        }
    }

    public File getImageDirFile(int projectId) throws IOException {
        File dir = new File(filePath + "/" + projectId + "/image");
        if (!dir.exists()) {
            FileUtils.forceMkdir(dir);
        }
        return dir;
    }

    public File getPdfDirectory(int projectId) throws IOException {
        File dir = new File(filePath + "/" + projectId + "/pdf");
        if (!dir.exists()) {
            FileUtils.forceMkdir(dir);
        }
        return dir;
    }

    public File getJsonDirectory(int projectId) throws IOException {
        File dir = new File(filePath + "/" + projectId + "/json");
        if (!dir.exists()) {
            FileUtils.forceMkdir(dir);
        }
        return dir;
    }

}
