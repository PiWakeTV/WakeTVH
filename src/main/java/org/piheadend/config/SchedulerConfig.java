package org.piheadend.config;

import org.piheadend.services.recording.RecordingInfo;
import org.piheadend.services.recording.RecordingInfoService;
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
    private static final String URL = "http://192.168.2.22:9981/api/dvr/entry/grid_upcoming";

    private RecordingInfoService recordingInfoService;

    @Scheduled(fixedRate = 10000)
    public void checkRecordings() {
        try {
            final List<RecordingInfo> info = recordingInfoService.gatherRecordingInfo();
            for(final RecordingInfo i : info) {
                log.info("Film: {}, Start: {}, Ende: {}", i.getTitle(), i.getStart(), i.getEnd());
            }
        } catch (IOException e) {
            log.error("JSON konnte nicht geladen werden.");
        }
    }

    @Autowired
    public void setRecordingInfoService(final RecordingInfoService recordingInfoService) {
        this.recordingInfoService = recordingInfoService;
    }
}
