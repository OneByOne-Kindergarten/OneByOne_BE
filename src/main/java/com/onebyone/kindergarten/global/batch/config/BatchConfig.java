package com.onebyone.kindergarten.global.batch.config;

import com.onebyone.kindergarten.domain.address.repository.RegionRepository;
import com.onebyone.kindergarten.domain.address.repository.SubRegionRepository;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.kindergatens.repository.KindergartenRepository;
import com.onebyone.kindergarten.global.batch.processor.KindergartenAddressProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final KindergartenRepository kindergartenRepository;
    private final RegionRepository regionRepository;
    private final SubRegionRepository subRegionRepository;

    @Bean
    public KindergartenAddressProcessor processor() {
//        return new KindergartenAddressProcessor();
        return KindergartenAddressProcessor.create(regionRepository, subRegionRepository);
    }

    @Bean
    public RepositoryItemReader<Kindergarten> kindergartenReader() {
//        JpaPagingItemReader<Kindergarten> reader = new JpaPagingItemReader<>();
//        reader.setEntityManagerFactory(entityManagerFactory);
//        reader.setMet
        RepositoryItemReader<Kindergarten> reader = new RepositoryItemReader<>();
        reader.setRepository(kindergartenRepository);
        reader.setMethodName("findAll");
        reader.setPageSize(100);

        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        reader.setSort(sorts);

        return reader;
    }

    @Bean
    public RepositoryItemWriter<Kindergarten> kindergartenWriter() {
        RepositoryItemWriter<Kindergarten> writer = new RepositoryItemWriter<>();
        writer.setRepository(kindergartenRepository);
        return writer;
    }

    @Bean
    public Step step1() {
        return new StepBuilder("AddressStep", jobRepository)
                .<Kindergarten, Kindergarten>chunk(100, transactionManager)
                .reader(kindergartenReader())
                .processor(processor())
                .writer(kindergartenWriter())
                .build();
    }
    @Bean
    public Job Job() {
        return new JobBuilder("AddressJob", jobRepository)
                .listener(new RunIdIncrementer())
                .start(step1())
                .build();
    }
}
