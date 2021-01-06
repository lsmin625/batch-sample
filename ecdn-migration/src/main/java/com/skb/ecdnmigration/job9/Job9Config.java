package com.skb.ecdnmigration.job9;

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


@Configuration 
@Import(AdminConfig.class)
public class Job9Config {
	@Autowired private StepBuilderFactory stepBuilderFactory;
	@Autowired private JobBuilderFactory jobBuilderFactory;
	@Autowired private JobFinishedListener jobFinishedListener;
	@Autowired private TriggerJobList triggerJobList;
	@Autowired @Qualifier("migDataSource") private DataSource migDataSource;
	

	@Value("${meta.admin-url}") private String adminUrl;
	@Value("${meta.callback-url}") private String callbackUrl;

	@Value("${job09.name}") private String jobName;
	@Value("${job09.desc}") private String jobDesc;
	@Value("${job09.mode}") private String jobMode;
	@Value("${job09.cron}") private String jobCron;

	@Value("${job09.output.csv}") private String csvFile;
	
	@Value("${data.limit:0}") private int dataLimit;
	    
    private ItemReader<TableContent> step1Reader() {
    	StringBuffer sql = new StringBuffer();
    	sql.append("SELECT C.content_seq, C.media_id, C.cid, C.package_info, M.meta_info, C.register_date, C.status, C.status_code, T.result_message");
    	sql.append(" FROM tb_content as C, tb_content_meta as M, tb_task T");
    	sql.append(" WHERE task_id = (select max(task_id)");
    	sql.append("                  from tb_task");
    	sql.append("                  where process_type = 'dl_download' ");
    	sql.append("					  and status = 'Done' ");
    	sql.append("					  and result_message ");
    	sql.append("					  like '%complete%' ");
    	sql.append("					  and job_id in (select job_id from tb_job where content_seq = C.content_seq )) ");
    	sql.append(" AND M.file_path = 'stb_info' ");
    	sql.append(" AND C.content_seq = M.content_seq ");
    	if(dataLimit > 0) {
        	sql.append(" LIMIT " + dataLimit + " OFFSET 0");
    	}
    	
    	JdbcCursorItemReader<TableContent> reader = new JdbcCursorItemReader<TableContent>();
        reader.setDataSource(migDataSource);
        reader.setRowMapper(new TableCntRowMapper());
        reader.setSql(sql.toString());
        return reader;
    }
 
    private Step step1() {
    	StepBuilder stepBuilder =  stepBuilderFactory.get("job09Step1");
        SimpleStepBuilder<TableContent, String> simpleStepBuilder = stepBuilder.<TableContent, String> chunk(50);
        simpleStepBuilder.reader(step1Reader());
        simpleStepBuilder.processor(new DbToJsonProcessor<TableContent, String>());
        simpleStepBuilder.writer(new JsonFileItemWriter(csvFile));
        return simpleStepBuilder.build();
    }

 	@Bean @Qualifier("job09")
    public Job job09() {

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