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

    private String pythonCommand;

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
        if (StringUtils.isEmpty(this.pythonCommand)) {
            String defaultPython3 = this.getDefaultPython3();
            if (StringUtils.isEmpty(defaultPython3)) {
                log.error("python3 command is not set.");
                throw new RuntimeException("No python3 command.");
            } else {
                log.info("python3 command is not set, use the default:[{}]", defaultPython3);
                this.pythonCommand = defaultPython3;
            }
        } else {
            if (!isPython3Command(this.pythonCommand)) {
                log.error("No qualified python3 command[{}] is configured.", this.pythonCommand);
                throw new RuntimeException(String.format("No qualified python3 command[%s] is configured.",
                        this.pythonCommand));
            } else {
                log.debug("python3 command is: [{}]", this.pythonCommand);
            }
        }
    }

    private String getDefaultPython3() {
        final String PYTHON3 = "python3";
        final String PYTHON = "python";
        if (isPython3Command(PYTHON3)) {
            return PYTHON3;
        } else if(isPython3Command(PYTHON)) {
            return PYTHON;
        } else {
            return null;
        }
    }

    boolean isPython3Command(String command) {
        if (StringUtils.isEmpty(command)) {
            return false;
        }
        try {
            ProcessBuilder builder = new ProcessBuilder( command, "-V");
            Process process = builder.start();
            String stdout = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
            String stderr = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
            log.debug("command:[{}], stdout:[{}], stderr:[{}]", command, stdout, stderr);
            log.debug("stdout: {}", stdout);
            log.debug("stderr: {}", stderr);
            return StringUtils.startsWith(stdout, "Python 3");
        } catch (Exception exception) {
            log.debug("command:[{}] failed.", command, exception);
            return false;
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
