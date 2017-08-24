package org.piheadend.config;

import org.piheadend.services.recording.RecordingInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
