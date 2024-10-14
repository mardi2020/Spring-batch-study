package com.mardi2020.exbatch.tasklet;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.NonNull;

@Slf4j
public class TimeCheckTask implements Tasklet, InitializingBean {

    private final LocalTime startTime;

    private final LocalTime endTime;

    private final ZoneId zoneId;

    @Builder
    public TimeCheckTask(LocalTime startTime, LocalTime endTime, ZoneId zoneId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.zoneId = zoneId;
    }

    @Override
    public RepeatStatus execute(@NonNull StepContribution contribution,
                                @NonNull ChunkContext chunkContext) throws Exception {
        log.info("오후 11시 ~ 11시 59분 사이이므로 작업을 수행합니다.");
        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        LocalTime currTime = now.toLocalTime();

        if (currTime.isBefore(startTime) || currTime.isAfter(endTime)) {
            throw new IllegalArgumentException(startTime + " 부터" + endTime + " 까지 작업이 가능합니다.");
        }
    }
}
