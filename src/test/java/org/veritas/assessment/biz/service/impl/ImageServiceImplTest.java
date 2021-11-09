package org.veritas.assessment.biz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.service.ImageService;
import org.veritas.assessment.system.config.VeritasProperties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class ImageServiceImplTest {

    @Autowired
    ImageService service;

    @Autowired
    VeritasProperties veritasProperties;

    private byte[] content;

    private final String sha256 = "a57db4fccc399b6f4d8418b891408e66996d50700cd63adf35993b674bd02313";

    @BeforeEach
    public void init() throws Exception {
        try (InputStream inputStream = new ClassPathResource("ftl/image/favicon.png").getInputStream()) {
            this.content = IOUtils.toByteArray(inputStream);
        }

    }

    @Test
    void testSaveImage_success() throws Exception {
        String url = service.saveImage(1, "test.png", content);
        log.info("url: {}", url);
        assertTrue(StringUtils.contains(url, sha256));

        // save again.
        url = service.saveImage(1, "test.png", content);
        log.info("url: {}", url);
        assertTrue(StringUtils.contains(url, sha256));


        FileUtils.delete(new File(veritasProperties.getImageDirFile(1), sha256 + ".png"));
    }

    @Test
    void testGetImage() throws Exception {
        String url = service.saveImage(1, "test.png", content);
        log.info("url: {}", url);

        byte[] loaded = service.getImage(1, sha256 + ".png");
        assertNotNull(loaded);
        assertEquals(sha256, DigestUtils.sha256Hex(loaded));
        FileUtils.delete(new File(veritasProperties.getImageDirFile(1), sha256 + ".png"));
    }

    @Test
    void testGetImage_notFoundFail() throws Exception {
        assertThrows(FileNotFoundException.class, () -> {
            service.getImage(1, "xxxx.png");
        });

    }
}