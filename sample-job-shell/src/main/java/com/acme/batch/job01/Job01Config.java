
package com.acme.batch.job01;

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
import com.sk.batch.lib.data.JobInfo;
import com.sk.batch.lib.data.JobInfoList;
import com.sk.batch.lib.service.JobFinishedListener;
import com.sk.batch.lib.service.ShellItemProcessor;
import com.sk.batch.lib.service.ShellItemReader;
import com.sk.batch.lib.service.ShellItemWriter;


@Configuration 
@Import(AdminConfig.class)
public class Job01Config {
	@Autowired private StepBuilderFactory stepBuilderFactory;
	@Autowired private JobBuilderFactory jobBuilderFactory;
	@Autowired private JobFinishedListener jobFinishedListener;
	@Autowired private JobInfoList jobInfoList;

	@Value("${meta.admin-url}") private String adminUrl;
	@Value("${meta.callback-url}") private String callbackUrl;

	@Value("${job01.name}") private String jobName;
	@Value("${job01.desc}") private String jobDesc;
	@Value("${job01.mode}") private String jobMode;
	@Value("${job01.cron}") private String jobCron;

	@Value("${job01.file.script}") private String script;

 	@Bean @Qualifier("job01")
    public Job job01() {
        JobInfo jobInfo = new JobInfo(jobName, callbackUrl);
        jobInfo.setDesc(jobDesc);
        jobInfo.setMode(jobMode);
        jobInfo.setCron(jobCron);
        jobInfo.setAdminUrl(adminUrl);

        JobBuilder jobBuilder = jobBuilderFactory.get(jobInfo.getName());
        jobBuilder.incrementer(new RunIdIncrementer());
        jobBuilder.preventRestart();
        jobBuilder.listener(jobFinishedListener);

        JobFlowBuilder jobFlowBuilder = jobBuilder.flow(step01());
        jobFlowBuilder.end();
        FlowJobBuilder flowJobBuilder = jobFlowBuilder.build();
        Job job = flowJobBuilder.build();

        jobInfo.setJob(job);
        jobInfoList.add(jobInfo);
        
        return job;
    }

 	private Step step01() {
 		StepBuilder stepBuilder =  stepBuilderFactory.get(jobName + "-step1");
        SimpleStepBuilder<String, String> simpleStepBuilder = stepBuilder.<String, String> chunk(1);
        simpleStepBuilder.reader(new ShellItemReader(script));
        simpleStepBuilder.processor(new ShellItemProcessor("euc-kr"));
        simpleStepBuilder.writer(new ShellItemWriter());

        return simpleStepBuilder.build();
	}
}