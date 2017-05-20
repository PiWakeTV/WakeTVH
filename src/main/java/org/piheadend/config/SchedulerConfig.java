package org.piheadend.config;

import org.piheadend.services.recording.RecordingInfo;
import org.piheadend.services.recording.RecordingInfoService;
import org.piheadend.services.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Configuration for the scheduler.
 *
 * 192.168.2.22:9981/api/dvr/entry/grid_upcoming
 *
 * @author aw
 * @since 25.02.2017
 */
@Component
public class SchedulerConfig {

    private static final Logger log = LoggerFactory.getLogger(SchedulerConfig.class);

    private RecordingInfoService recordingInfoService;
    private StorageService storageService;

    @Scheduled(fixedRate = 10000)
    public void checkRecordings() {
        try {
            final List<RecordingInfo> info = recordingInfoService.gatherRecordingInfo();
            for(final RecordingInfo i : info) {
                log.info("Film: {}, Start: {}, Ende: {}", i.getTitle(), i.getStart(), i.getEnd());
            }
            storageService.saveToDisk(info);
        } catch (IOException e) {
            log.error("JSON konnte nicht geladen werden.");
        }
    }

    @Autowired
    public void setRecordingInfoService(final RecordingInfoService recordingInfoService) {
        this.recordingInfoService = recordingInfoService;
    }

    @Autowired
    public void setStorageService(final StorageService storageService) {
        this.storageService = storageService;
    }
}
