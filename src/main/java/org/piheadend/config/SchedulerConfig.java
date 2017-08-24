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
 * Configuration for the schedulers.
 *
 * @author aw
 * @since 25.02.2017
 */
@Component
public class SchedulerConfig {

    private RecordingInfoService recordingInfoService;

    @Scheduled(fixedRate = 10000)
    public void pollForRecordings() {
        recordingInfoService.updateRecordings();
    }

    @Autowired
    public void setRecordingInfoService(final RecordingInfoService recordingInfoService) {
        this.recordingInfoService = recordingInfoService;
    }
}
