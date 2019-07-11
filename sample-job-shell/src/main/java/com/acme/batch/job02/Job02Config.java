package com.acme.batch.job02;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.JobFlowBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.sk.batch.lib.AdminConfig;
import com.sk.batch.lib.JobFinishedListener;
import com.sk.batch.lib.ShellItemProcessor;
import com.sk.batch.lib.ShellItemReader;
import com.sk.batch.lib.ShellItemWriter;
import com.sk.batch.lib.TriggerJobInfo;
import com.sk.batch.lib.TriggerJobRegister;


@Configuration 
@Import(AdminConfig.class)
public class Job02Config {
	
	@Autowired private StepBuilderFactory stepBuilderFactory;
	@Autowired private JobBuilderFactory jobBuilderFactory;
	@Autowired private JobFinishedListener jobFinishedListener;
	@Autowired private TriggerJobRegister triggerJobRegister;

	@Value("${spring.application.name}") private String jobGroup;
	@Value("${job02.name}") private String jobName;
	@Value("${job02.desc}") private String jobDesc;
	@Value("${job02.mode}") private String jobMode;
	@Value("${job02.cron}") private String jobCron;
	@Value("${job02.file.script}") private String script;

 	private Step buildStep01() {
 		StepBuilder stepBuilder = stepBuilderFactory.get("shellJob02Step1");
        SimpleStepBuilder<String, String> simpleStepBuilder = stepBuilder.<String, String> chunk(1);
        simpleStepBuilder.reader(new ShellItemReader(script));
        simpleStepBuilder.processor(new ShellItemProcessor());
        simpleStepBuilder.writer(new ShellItemWriter());
        return simpleStepBuilder.build();
	}

 	@Bean @Qualifier("buildJob02")
    public TriggerJobInfo buildJob02() {
        TriggerJobInfo jobInfo = new TriggerJobInfo();
 		jobInfo.setGroup(jobGroup);
        jobInfo.setName(jobName);
        jobInfo.setDesc(jobDesc);
        jobInfo.setMode(jobMode);
        jobInfo.setCron(jobCron);

        JobBuilder jobBuilder = jobBuilderFactory.get(jobInfo.getJobKey());
        jobBuilder.incrementer(new RunIdIncrementer());
        jobBuilder.preventRestart();
        jobBuilder.listener(jobFinishedListener);

        JobFlowBuilder jobFlowBuilder = jobBuilder.flow(buildStep01());
        jobFlowBuilder.end();
        
        FlowJobBuilder flowJobBuilder = jobFlowBuilder.build();
        Job job = flowJobBuilder.build();

        jobInfo.setJob(job);
        triggerJobRegister.add(jobInfo);
        return jobInfo;
    }
 }