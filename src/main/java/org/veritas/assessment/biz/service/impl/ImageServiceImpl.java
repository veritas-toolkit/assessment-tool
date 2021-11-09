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

package org.veritas.assessment.biz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.veritas.assessment.biz.service.ImageService;
import org.veritas.assessment.common.exception.FileSystemException;
import org.veritas.assessment.system.config.VeritasProperties;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

    @Autowired
    VeritasProperties veritasProperties;

    @Override
    public byte[] getImage(int projectId, String image) throws IOException {
        File dir = null;
        try {
            dir = veritasProperties.getImageDirFile(projectId);
        } catch (IOException exception) {
            log.warn("Not found the image{} of project[{}]", image, projectId);
            throw new FileNotFoundException("Not found the image.");
        }
        File imageFile = new File(dir, image);
        if (!imageFile.isFile()) {
            log.warn("Not found the image{} of project[{}]", image, projectId);
            throw new FileNotFoundException("Not found the image.");
        }
        byte[] content = null;
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(imageFile))) {
            content = IOUtils.toByteArray(inputStream);
        } catch (IOException exception) {
            throw new IOException("Load the image failed.");
        }
        return content;
    }

    @Override
    public String saveImage(int projectId, String imageName, byte[] content) {
        String sha256 = DigestUtils.sha256Hex(content);
        File dir = null;
        try {
            dir = veritasProperties.getImageDirFile(projectId);
        } catch (IOException exception) {
            log.warn("Not found the image directory of project[{}].", projectId, exception);
            throw new FileSystemException("Cannot upload the image.", exception);
        }
        String suffix = FilenameUtils.getExtension(imageName);
        String filename = sha256 + "." + suffix;
        File imageFile = new File(dir, filename);
        if (!imageFile.exists()) {
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(imageFile))) {
                IOUtils.write(content, outputStream);
            } catch (IOException exception) {
                log.warn("Write the image[{}] to filesystem failed.", imageFile.getAbsolutePath(), exception);
                throw new FileSystemException("Save the image failed.", exception);
            }
        }
        return String.format("api/project/%d/image/%s", projectId, filename);
    }
}
