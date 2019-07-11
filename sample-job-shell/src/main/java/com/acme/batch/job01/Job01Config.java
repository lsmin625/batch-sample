package com.acme.batch.job01;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.sk.batch.lib.AdminConfig;
import com.sk.batch.lib.ShellItemProcessor;
import com.sk.batch.lib.ShellItemReader;
import com.sk.batch.lib.ShellItemWriter;
import com.sk.batch.lib.TriggerJobInfo;
import com.sk.batch.lib.TriggerJobRegister;


@Configuration 
@Import(AdminConfig.class)
public class Job01Config {
	
	@Autowired private StepBuilderFactory stepBuilderFactory;
	@Autowired private TriggerJobRegister triggerJobRegister;

	@Value("${spring.application.name}") private String jobGroup;
	@Value("${job01.name}") private String jobName;
	@Value("${job01.desc}") private String jobDesc;
	@Value("${job01.mode}") private String jobMode;
	@Value("${job01.cron}") private String jobCron;
	@Value("${job01.file.script}") private String script;

 	private Step buildStep01() {
 		StepBuilder stepBuilder =  stepBuilderFactory.get("shellJob01Step1");
        SimpleStepBuilder<String, String> simpleStepBuilder = stepBuilder.<String, String> chunk(1);
        simpleStepBuilder.reader(new ShellItemReader(script));
        simpleStepBuilder.processor(new ShellItemProcessor());
        simpleStepBuilder.writer(new ShellItemWriter());

        return simpleStepBuilder.build();
	}

 	@Bean @Qualifier("buildJob01")
    public TriggerJobInfo buildJob01() {

 		TriggerJobInfo jobInfo = new TriggerJobInfo();
 		jobInfo.setGroup(jobGroup);
        jobInfo.setName(jobName);
        jobInfo.setDesc(jobDesc);
        jobInfo.setMode(jobMode);
        jobInfo.setCron(jobCron);
        jobInfo.addStep(buildStep01());

        triggerJobRegister.add(jobInfo);

        return jobInfo;
    }
}