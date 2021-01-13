package com.skb.ecdnmigration.job6;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import com.sk.batch.lib.AdminConfig;
import com.sk.batch.lib.data.TriggerJobInfo;
import com.sk.batch.lib.data.TriggerJobList;
import com.sk.batch.lib.service.JobFinishedListener;
import com.skb.ecdnmigration.job.data.FileList;
import com.skb.ecdnmigration.job.data.TableContent;
import com.skb.ecdnmigration.job1.Job1Config;
import com.skb.ecdnmigration.job1.MultiFileItemWriter;

@Configuration 
@Import({AdminConfig.class, Job1Config.class})
public class Job6Config {
	private Logger logger = LoggerFactory.getLogger(Job6Config.class);

	@Autowired private StepBuilderFactory stepBuilderFactory;
	@Autowired private JobBuilderFactory jobBuilderFactory;
	@Autowired private JobFinishedListener jobFinishedListener;
	@Autowired private TriggerJobList triggerJobList;
	@Autowired @Qualifier("migDataSource") private DataSource migDataSource;
	@Autowired @Qualifier("jobJdbcTemplate") private NamedParameterJdbcTemplate jobJdbcTemplate;

	@Value("${meta.admin-url}") private String adminUrl;
	@Value("${meta.callback-url}") private String callbackUrl;

	@Value("${job06.name}") private String jobName;
	@Value("${job06.desc}") private String jobDesc;
	@Value("${job06.mode}") private String jobMode;
	@Value("${job06.cron}") private String jobCron;

	@Value("${job06.file.output-common}") private String fCommon;
	@Value("${job06.file.output-video}") private String fVideo;
	@Value("${job06.file.output-audio}") private String fAudio;
	@Value("${job06.file.output-caption}") private String fCaption;

	@Value("${data.limit}") private int dataLimit = 0;

    private ItemReader<TableContent> contentReader() {
    	StringBuffer sql = new StringBuffer();
    	sql.append("SELECT content_seq, media_id, cid, package_info, register_date ");
    	sql.append(" FROM tb_content ");
       	sql.append(" WHERE status_code = 7 AND modify_date >= '2019-01-01 00:00:00'");
    	if(dataLimit > 0) {
        	sql.append("LIMIT " + dataLimit + " OFFSET 0");
    	}
    	
    	JdbcCursorItemReader<TableContent> reader = new JdbcCursorItemReader<TableContent>();
        reader.setDataSource(migDataSource);
        reader.setRowMapper(new ContentRowMapper());
        reader.setSql(sql.toString());
        return reader;
    }

    private Step stepContentSize() {
    	StepBuilder stepBuilder =  stepBuilderFactory.get("stepContentSize");
        SimpleStepBuilder<TableContent, FileList> simpleStepBuilder = stepBuilder.<TableContent, FileList> chunk(100);
        simpleStepBuilder.reader(contentReader());
        simpleStepBuilder.processor(new ContentToCvsProcessor<TableContent, FileList>(jobJdbcTemplate));
        simpleStepBuilder.writer(new MultiFileItemWriter(fCommon, fVideo, fAudio, fCaption));
        return simpleStepBuilder.build();
    }

  	@Bean @Qualifier("ecdnJob06")
    public Job job06() {

 		JobBuilder jobBuilder = jobBuilderFactory.get(jobName);
        jobBuilder.incrementer(new RunIdIncrementer());
        jobBuilder.preventRestart();
        jobBuilder.listener(jobFinishedListener);

        JobFlowBuilder jobFlowBuilder = jobBuilder.flow(stepContentSize());
        jobFlowBuilder.end();
        
        FlowJobBuilder flowJobBuilder = jobFlowBuilder.build();
        Job job = flowJobBuilder.build();

        TriggerJobInfo jobInfo = new TriggerJobInfo(job.getName(), callbackUrl);
        jobInfo.setDesc(jobDesc);
        jobInfo.setMode(jobMode);
        jobInfo.setCron(jobCron);
        jobInfo.setAdminUrl(adminUrl);
        jobInfo.setJob(job);
        triggerJobList.add(jobInfo);
        
        return job;
    }   
 }