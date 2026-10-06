package org.practice.linkedinbatch;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DeliverPackageJobConfiguration {

    @Bean
    public JobExecutionDecider decider() {
        return new DeliveryDecider();
    }

    @Bean
    public JobExecutionDecider rightItemDecider() {
        return new RightItemDecider();
    }

    @Bean
    public Step thanksCustomerStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("thanksCustomerStep", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Thanks Customer Step");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step refundCustomerStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("refundCustomerStep", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Refund Customer Step");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step leavePackageAtDoorStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("leavePackageAtDoorStep", jobRepository)
                .tasklet((contribution, chunkContext) ->
                {
                    System.out.println("Leaving the package at the door");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }


    @Bean
    public Step storePackageStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sotrePackageStep", jobRepository)
                .tasklet((contribution, chunkContext) ->
                {
                    System.out.println("Storing a package because we're lost");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step givePackageToCustomerStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("givePackageToCustomerStep", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Package given to the customer.");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step driveToAddressStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        boolean gotLost = false;

        return new StepBuilder("driveToAddressStep", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    if (gotLost) {
                        throw new RuntimeException("Got lost driving to the address");
                    }
                    System.out.println("Successfuly arrived at the adress");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step packageItemStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("packageItemStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.printf("Job Parameters : %s", chunkContext.getStepContext().getJobParameters());
                    String item = chunkContext.getStepContext().getJobParameters().get("item").toString();
                    String date = chunkContext.getStepContext().getJobParameters().get("run.date").toString();
                    System.out.printf("The %s, has been packaged on %s", item, date);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Job deliverPackageJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("deliverPackageJob", jobRepository)
                .start(packageItemStep(jobRepository, transactionManager))
                .next(driveToAddressStep(jobRepository, transactionManager))
                    .on("FAILED").to(storePackageStep(jobRepository,transactionManager))
                .from(driveToAddressStep(jobRepository,transactionManager))
                    .on("*").to(decider())
                        .on("PRESENT").to(givePackageToCustomerStep(jobRepository, transactionManager))
                    .next(rightItemDecider())
                        .on("RIGHT_ITEM").to(thanksCustomerStep(jobRepository, transactionManager))
                        .from(rightItemDecider()).on("WRONG_ITEM").to(refundCustomerStep(jobRepository, transactionManager))
                    .from(decider())
                    .on("NOT_PRESENT").to(leavePackageAtDoorStep(jobRepository, transactionManager))
                .end()
                .build();
    }

}
