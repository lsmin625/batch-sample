package com.skb.ecdnmigration.job3;

import javax.sql.DataSource;

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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.sk.batch.lib.AdminConfig;
import com.sk.batch.lib.JobFinishedListener;
import com.sk.batch.lib.TriggerJobInfo;
import com.sk.batch.lib.TriggerJobList;
import com.skb.ecdnmigration.job.data.TableContent;
import com.skb.ecdnmigration.job1.TableRowMapper;
import com.skb.ecdnmigration.job.data.FileCheck;


@Configuration 
@Import(AdminConfig.class)
public class Job3Config {
	@Autowired private StepBuilderFactory stepBuilderFactory;
	@Autowired private JobBuilderFactory jobBuilderFactory;
	@Autowired private JobFinishedListener jobFinishedListener;
	@Autowired private TriggerJobList triggerJobList;
	@Autowired @Qualifier("migDataSource") private DataSource migDataSource;
	

	@Value("${meta.admin-url}") private String adminUrl;
	@Value("${meta.callback-url}") private String callbackUrl;

	@Value("${job03.name}") private String jobName;
	@Value("${job03.desc}") private String jobDesc;
	@Value("${job03.mode}") private String jobMode;
	@Value("${job03.cron}") private String jobCron;

	@Value("${job03.file.output-check}") private String fCheck;
	
	@Value("${data.limit}") private int dataLimit = 0;
	    
    private ItemReader<TableContent> step1Reader() {
    	StringBuffer sql = new StringBuffer();
       	sql.append("SELECT tb_content.content_seq, media_id, cid, package_info, meta_info, tb_content.register_date ");
    	sql.append("FROM tb_content, tb_content_meta ");
       	sql.append("WHERE tb_content.status_code = 7 ");
       	sql.append(" AND tb_content.content_seq = tb_content_meta.content_seq ");
       	sql.append(" AND tb_content_meta.file_path = 'stb_info' ");
    	if(dataLimit > 0) {
        	sql.append(" LIMIT " + dataLimit + " OFFSET 0");
    	}
    	
    	JdbcCursorItemReader<TableContent> reader = new JdbcCursorItemReader<TableContent>();
        reader.setDataSource(migDataSource);
        reader.setRowMapper(new TableRowMapper());
        reader.setSql(sql.toString());
        return reader;
    }
 
    private Step step1() {
    	StepBuilder stepBuilder =  stepBuilderFactory.get("migCheckStep1");
        SimpleStepBuilder<TableContent, FileCheck> simpleStepBuilder = stepBuilder.<TableContent, FileCheck> chunk(100);
        simpleStepBuilder.reader(step1Reader());
        simpleStepBuilder.processor(new DbToCvsProcessor<TableContent, FileCheck>());
        simpleStepBuilder.writer(new SingleFileItemWriter(fCheck));
        return simpleStepBuilder.build();
    }

 	@Bean @Qualifier("migCheckJob")
    public Job job03() {

 		JobBuilder jobBuilder = jobBuilderFactory.get(jobName);
        jobBuilder.incrementer(new RunIdIncrementer());
        jobBuilder.preventRestart();
        jobBuilder.listener(jobFinishedListener);

        JobFlowBuilder jobFlowBuilder = jobBuilder.flow(step1());
        jobFlowBuilder.end();
        
        FlowJobBuilder flowJobBuilder = jobFlowBuilder.build();
        Job job = flowJobBuilder.build();

        TriggerJobInfo jobInfo = new TriggerJobInfo();
        jobInfo.setName(job.getName());
        jobInfo.setDesc(jobDesc);
        jobInfo.setMode(jobMode);
        jobInfo.setCron(jobCron);
        jobInfo.setAdminUrl(adminUrl);
        jobInfo.setCallbackUrl(callbackUrl);
        jobInfo.setJob(job);
        triggerJobList.add(jobInfo);
        
        return job;
    }   
 }