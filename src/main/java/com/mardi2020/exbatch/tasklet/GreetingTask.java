package com.mardi2020.exbatch.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.NonNull;


@Slf4j
public class GreetingTask implements Tasklet, InitializingBean {

    /**
     * Tasklet의 구현 메서드
     * @param contribution mutable state to be passed back to update the current step
     * execution
     * @param chunkContext attributes shared between invocations but not between restarts
     * @return RepeatStatus (FINISHED: 태스크릿이 종료됨, CONTINUABLE: 계속해서 태스크 수행,
     *          continueIf(condition): 조건에 따라 종료할지 지속할지 결정하는 메소드이며 종료/지속 결정함)
     * @throws Exception e
     */
    @Override
    public RepeatStatus execute(@NonNull StepContribution contribution,
                                @NonNull ChunkContext chunkContext) throws Exception {

        log.info("------------- Task Excute ---------------");
        log.info("GreetingTask: {}, {}", contribution, chunkContext);
        return RepeatStatus.FINISHED;
    }

    /**
     * InitializingBean의 메서드
     * task를 수행할때 프로퍼티를 설정하고 난 뒤, 수행되는 메서드 없어도 됨!
     * @throws Exception e
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info(" ----------- After Properties Sets() ----------");
    }
}
