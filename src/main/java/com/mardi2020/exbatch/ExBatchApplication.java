package com.mardi2020.exbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class ExBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExBatchApplication.class, args);
    }

}
