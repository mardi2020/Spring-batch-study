package com.mardi2020.exbatch.config;

import com.mardi2020.exbatch.chunk.AggregateCustomerProcessor;
import com.mardi2020.exbatch.chunk.data.Customer;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class FlatFileItemJobConfig {

    public static final int CHUNK_SIZE = 100;

    public static final String ENCODING = "UTF-8";

    public static final String FLAT_FILE_WRITER_CHUNK_JOB = "FLAT_FILE_WRITER_CHUNK_JOB";

    private final ConcurrentHashMap<String, Integer> aggregateInfos = new ConcurrentHashMap<>();

    private final ItemProcessor<Customer, Customer> itemProcessor = new AggregateCustomerProcessor(aggregateInfos);

    @Bean
    public FlatFileItemReader<Customer> flatFileItemReader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("FlatFileItemReader")
                .resource(new ClassPathResource("customer.csv"))
                .encoding(ENCODING)
                .delimited().delimiter(",")
                .names("name", "age", "gender")
                .targetType(Customer.class)
                .build();
    }

    @Bean
    public FlatFileItemWriter<Customer> flatFileItemWriter() {
        return new FlatFileItemWriterBuilder<Customer>()
                .name("flatFileItemWriter")
                .resource(new FileSystemResource("./output/customer_new.csv"))
                .encoding(ENCODING)
                .delimited().delimiter("\t")
                .names("Name", "Age", "Gender")
                .append(false)
                .lineAggregator(customer -> customer.getName() + "," + customer.getAge()) // implements LineAggregator<T>
                .headerCallback(writer -> writer.write("ID,AGE")) // implements FlatFileHeaderCallback
                .footerCallback(writer -> { // implements FlatFileFooterCallback
                    writer.write("총 고객 수: " + aggregateInfos.get("TOTAL_CUSTOMERS"));
                    writer.write(System.lineSeparator());
                    writer.write("총 나이: " + aggregateInfos.get("TOTAL_AGES"));
                })
                .build();
    }


    @Bean
    public Step flatFileStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("------------------ Init flatFileStep -----------------");

        return new StepBuilder("flatFileStep", jobRepository)
                .<Customer, Customer>chunk(CHUNK_SIZE, transactionManager)
                .reader(flatFileItemReader())
                .processor(itemProcessor) //
                .faultTolerant()
                .skipPolicy((exception, skipCount) -> exception instanceof FlatFileParseException) // skip 정책은 faultTolerant() 호출 후 수행
                .writer(flatFileItemWriter())
                .build();
    }

    @Bean
    public Job flatFileJob(Step flatFileStep, JobRepository jobRepository) {
        log.info("------------------ Init flatFileJob -----------------");
        return new JobBuilder(FLAT_FILE_WRITER_CHUNK_JOB, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(flatFileStep)
                .build();
    }
}
