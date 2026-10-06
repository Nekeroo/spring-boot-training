package org.practice.linkedinbatch;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.step.tasklet.Tasklet;

import java.util.Random;

public class RightItemDecider implements JobExecutionDecider {

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, @Nullable StepExecution stepExecution) {
        String status = new Random().nextFloat() > 0.7f ? "RIGHT_ITEM" : "WRONG_ITEM";
        System.out.printf("The item is %s\n", status);
        return new FlowExecutionStatus(status);
    }

}

