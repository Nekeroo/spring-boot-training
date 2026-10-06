package org.practice.linkedinbatch;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlowersJobConfiguration {

    @Bean
    public StepExecutionListener selectFlowerListener() {
        return new SelectFlowerListener();
    }

    @Bean
    public Step selectFlowersStep(JobRepository jobRepository) {
        return new StepBuilder("selectFlowersStep", jobRepository).tasklet(new Tasklet() {

            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("Gathering flowers for order.");
                return RepeatStatus.FINISHED;
            }

        }).listener(selectFlowerListener()).build();
    }

    @Bean
    public Step removeThornsStep(JobRepository jobRepository) {
        return new StepBuilder("removeThornsStep", jobRepository).tasklet(new Tasklet() {

            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("Remove thorns from roses.");
                return RepeatStatus.FINISHED;
            }

        }).build();
    }

    @Bean
    public Step arrangeFlowersStep(JobRepository jobRepository) {
        return new StepBuilder("arrangeFlowersStep", jobRepository).tasklet(new Tasklet() {

            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("Arranging flowers for order.");
                return RepeatStatus.FINISHED;
            }

        }).build();
    }

    @Bean
    public Job prepareFlowers(JobRepository jobRepository) {
        return new JobBuilder("prepareFlowers", jobRepository)
                .start(selectFlowersStep(jobRepository))
                .on("TRIM_REQUIRED").to(removeThornsStep(jobRepository)).next(arrangeFlowersStep(jobRepository))
                .from(selectFlowersStep(jobRepository))
                .on("NO_TRIM_REQUIRED").to(arrangeFlowersStep(jobRepository))
                .end()
                .build();
    }


}
