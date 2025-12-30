package com.example.lunchdecider.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchStartupRunner {

    @Bean
    ApplicationRunner runPredefinedUsersJob(JobLauncher jobLauncher, Job predefinedUsersJob) {
        return args -> jobLauncher.run(
                predefinedUsersJob,
                new JobParametersBuilder()
                        .addLong("run.id", System.currentTimeMillis()) // unique params every startup
                        .toJobParameters()
        );
    }
}
