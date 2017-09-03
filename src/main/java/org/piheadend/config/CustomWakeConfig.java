package org.piheadend.config;

import org.piheadend.services.wake.WakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Configuration
public class CustomWakeConfig implements SchedulingConfigurer {

    private final WakeService wakeService;
    private final String cron;

    @Autowired
    public CustomWakeConfig(final WakeService wakeService, final @Value("${wake.cron.expression}") String cron) {
        Assert.notNull(wakeService, "The WakeService must not be 'null'.");
        this.wakeService = wakeService;
        this.cron = cron;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        if(StringUtils.hasText(cron)) {
            taskRegistrar.addCronTask(customCronTask());
        }
    }

    @Bean
    @SuppressWarnings("WeakerAccess")
    CronTask customCronTask() {

        if(StringUtils.hasText(cron)) {
            return new CronTask(this.wakeService::wake, this.cron);
        } else {
            return null;
        }
    }
}
