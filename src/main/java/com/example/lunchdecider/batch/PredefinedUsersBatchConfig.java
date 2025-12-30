package com.example.lunchdecider.batch;

import com.example.lunchdecider.domain.UserAccount;
import com.example.lunchdecider.repo.UserAccountRepository;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.*;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class PredefinedUsersBatchConfig {

    @Value("${app.predefined-users.csv}")
    private Resource usersCsv;

    @Bean
    public FlatFileItemReader<PredefinedUserRow> predefinedUsersReader() {
        FlatFileItemReader<PredefinedUserRow> reader = new FlatFileItemReader<>();
        reader.setResource(usersCsv);
        reader.setLinesToSkip(1);

        DefaultLineMapper<PredefinedUserRow> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("username");
        tokenizer.setDelimiter(",");

        BeanWrapperFieldSetMapper<PredefinedUserRow> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(PredefinedUserRow.class);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(mapper);

        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public ItemProcessor<PredefinedUserRow, UserAccount> predefinedUsersProcessor(UserAccountRepository repo) {
        return row -> {
            String u = row.getUsername() == null ? "" : row.getUsername().trim();
            if (u.isBlank()) return null;
            if (repo.existsByUsername(u)) return null; // idempotent
            return new UserAccount(u);
        };
    }

    @Bean
    public RepositoryItemWriter<UserAccount> predefinedUsersWriter(UserAccountRepository repo) {
        RepositoryItemWriter<UserAccount> writer = new RepositoryItemWriter<>();
        writer.setRepository(repo);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step loadUsersStep(JobRepository jobRepository,
                              PlatformTransactionManager txManager,
                              ItemReader<PredefinedUserRow> reader,
                              ItemProcessor<PredefinedUserRow, UserAccount> processor,
                              ItemWriter<UserAccount> writer) {
        return new StepBuilder("loadUsersStep", jobRepository)
                .<PredefinedUserRow, UserAccount>chunk(10, txManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job predefinedUsersJob(JobRepository jobRepository, Step loadUsersStep) {
        return new JobBuilder("predefinedUsersJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(loadUsersStep)
                .build();
    }
}
