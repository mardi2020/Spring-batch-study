package com.mardi2020.exbatch.config;

import com.mardi2020.exbatch.tasklet.GreetingTask;
import com.mardi2020.exbatch.tasklet.TimeCheckTask;
import java.time.LocalTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BasicTaskJobConfiguration {

    private final PlatformTransactionManager transactionManager;

    @Bean
    public Tasklet greetingTasklet() {
        return new GreetingTask();
    }

    @Bean
    public Tasklet timeCheckTask() {
        return TimeCheckTask.builder()
                .startTime(LocalTime.of(23, 0))
                .endTime(LocalTime.of(23, 59))
                .zoneId(ZoneId.of("Asia/Seoul"))
                .build();
    }

    /**
     * step을 spring bean으로 등록
     * @param jobRepository 배치 작업의 상태를 저장하고 관리
     * @return 생성된 step
     */
//    @Bean
    public Step myStep(JobRepository jobRepository) {
        return new StepBuilder("myStep", jobRepository)
                .tasklet(greetingTasklet(), transactionManager)
                .build();
    }

    /**
     * step, jobRepository가 필요한 job을 생성
     * incrementer: 잡이 지속적으로 실행될 때 잡의 unique함을 보장하는 방법 설정
     * RunIdIncrementer: 잡의 id를 지속적으로 증가시키면서 unique함 유지
     * start(step): job의 시작포인트를 잡음
     * @param myStep step 정보
     * @param jobRepository job을 등록함
     * @return job 빌더로 생성
     */
//    @Bean
    public Job myJob(Step myStep, JobRepository jobRepository) {
        return new JobBuilder("myJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(myStep)
                .build();
    }

    @Bean
    public Step timeCheckStep1(JobRepository jobRepository) {
        return new StepBuilder("timeCheckStep", jobRepository)
                .tasklet(timeCheckTask(), transactionManager)
                .build();
    }


    @Bean
    public Job timeCheckJob(Step timeCheckStep1, JobRepository jobRepository) {
        return new JobBuilder("timeCheckJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(timeCheckStep1)
                .build();
    }
}
