package org.veritas.assessment.biz.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class CacheScheduledTask {
    @Autowired
    private CacheManager cacheManager;

    @Scheduled(fixedRate = 5 * 1000)
    public void clearCacheSchedule() {
        if (!needCleanCache()) {
            return;
        }
        log.info("clear cache start...");
        for (String name : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        }
        log.info("clear cache has finished.");

    }

    private boolean needCleanCache() {
        File file = new File("file/db/.clear_cache");
        boolean result = file.exists();
        if (result) {
            FileUtils.deleteQuietly(file);
        }
        if (log.isTraceEnabled()) {
            if (result) {
                log.trace("need to clear cache.");
            } else {
                log.trace("not need to clear cache.");
            }
        }
        return result;
    }
}
