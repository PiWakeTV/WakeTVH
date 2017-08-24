package org.piheadend.config;

import org.piheadend.services.recording.RecordingInfoService;
import org.piheadend.services.wake.WakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Configuration for the schedulers.
 *
 * @author aw
 * @since 25.02.2017
 */
@Component
public class SchedulerConfig {

    private final RecordingInfoService recordingInfoService;
    private final WakeService wakeService;

    @Autowired
    public SchedulerConfig(final RecordingInfoService recordingInfoService, final WakeService wakeService) {
        Assert.notNull(recordingInfoService, "RecordingInfoService must not be 'null'.");
        Assert.notNull(wakeService, "WakeService must not be 'null'.");
        this.recordingInfoService = recordingInfoService;
        this.wakeService = wakeService;
    }

    /**
     * Polls for recordings on the TVHeadend server in a fixed rate.
     */
    @Scheduled(fixedRate = 10000)
    public void pollForRecordings() {
        recordingInfoService.updateRecordings();
    }

    /**
     * Polls for required wake-ups.
     */
    @Scheduled(fixedRate = 10000)
    public void pollForWakes() {
        wakeService.checkWake();
    }

    /**
     * Custom wake-ups by cron expression.
     */
    @Scheduled(cron = "${wake.cron.expression}")
    public void customWakes() {
        wakeService.wake();
    }
}
