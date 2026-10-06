package org.practice.linkedinbatch;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.step.StepExecution;

import java.time.LocalDateTime;

public class DeliveryDecider implements JobExecutionDecider {

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, @Nullable StepExecution stepExecution) {
        String result = LocalDateTime.now().getHour() < 12 ? "PRESENT": "NOT_PRESENT";
        System.out.printf("Decider result is %s\n", result);
        return new FlowExecutionStatus(result);
    }

}
