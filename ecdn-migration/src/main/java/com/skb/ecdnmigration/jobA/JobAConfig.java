package com.skb.ecdnmigration.jobA;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.batch.item.support.PassThroughItemProcessor;
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
import com.skb.ecdnmigration.job.data.TableContent;
import com.skb.ecdnmigration.job.data.TableJobId;


@Configuration 
@Import(AdminConfig.class)
public class JobAConfig {
	@Autowired private StepBuilderFactory stepBuilderFactory;
	@Autowired private JobBuilderFactory jobBuilderFactory;
	@Autowired private JobFinishedListener jobFinishedListener;
	@Autowired private TriggerJobList triggerJobList;
	@Autowired @Qualifier("migDataSource") private DataSource migDataSource;
	@Autowired @Qualifier("migJdbcTemplate") private NamedParameterJdbcTemplate migJdbcTemplate;
	

	@Value("${meta.admin-url}") private String adminUrl;
	@Value("${meta.callback-url}") private String callbackUrl;

	@Value("${jobA.name}") private String jobName;
	@Value("${jobA.desc}") private String jobDesc;
	@Value("${jobA.mode}") private String jobMode;
	@Value("${jobA.cron}") private String jobCron;

	@Value("${jobA.output.csv}") private String csvFile;
	
	@Value("${data.limit:0}") private int dataLimit;
	
	private List<TableJobId> jobIdList = new ArrayList<TableJobId>();
	    
    private ItemReader<TableContent> step1Reader() {
    	StringBuffer sql = new StringBuffer();
    	sql.append("SELECT C.content_seq, C.media_id, C.cid, C.package_info, M.meta_info, C.register_date, C.status, C.status_code, T.result_message");
    	sql.append(" FROM tb_content as C, tb_content_meta as M, tb_task T");
    	sql.append(" WHERE task_id = (select max(task_id)");
    	sql.append("                  from tb_task");
    	sql.append("                  where process_type = 'dl_download' ");
    	sql.append("					  and status = 'Done' ");
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
    	StepBuilder stepBuilder =  stepBuilderFactory.get("jobAStep1");
        SimpleStepBuilder<TableContent, String> simpleStepBuilder = stepBuilder.<TableContent, String> chunk(50);
        simpleStepBuilder.reader(step1Reader());
        simpleStepBuilder.processor(new DbToJsonProcessor<TableContent, String>());
        simpleStepBuilder.writer(new JsonFileItemWriter(csvFile));
        return simpleStepBuilder.build();
    }

    private ItemReader<TableJobId> step2Reader() {
    	StringBuffer sql = new StringBuffer();
    	sql.append("select max(vj.job_id) as job_id, vj.content_seq as content_seq from ( ");
    	sql.append("		  select j.job_id, j.status, j.process_parameters, j.content_seq from tb_job as j where j.content_seq ");
    	sql.append("		    in (select content_seq FROM tb_content where content_seq ");
    	sql.append("		    not in (select c.content_seq ");
    	sql.append("		    from tb_content as c, tb_content_meta as m ");
    	sql.append("		    where c.content_seq = m.content_seq and m.file_path = 'stb_info')) ");
    	sql.append("		  ) as vj group by vj.content_seq ");
    	
    	JdbcCursorItemReader<TableJobId> reader = new JdbcCursorItemReader<TableJobId>();
        reader.setDataSource(migDataSource);
        reader.setRowMapper(new TableJobIdMapper());
        reader.setSql(sql.toString());

        return reader;
    }

    private Step step2() {
    	StepBuilder stepBuilder =  stepBuilderFactory.get("jobAStep2");
        SimpleStepBuilder<TableJobId, TableJobId> simpleStepBuilder = stepBuilder.<TableJobId, TableJobId> chunk(100);
        simpleStepBuilder.reader(step2Reader());
        simpleStepBuilder.processor(new PassThroughItemProcessor<TableJobId>());
        simpleStepBuilder.writer(new ListMemoryWriter(jobIdList));
        return simpleStepBuilder.build();
    }

    private ItemReader<TableContent> step3Reader() {
    	MemoryItemReader reader = new MemoryItemReader(jobIdList, migJdbcTemplate);
    	return reader;
    }

    private Step step3() {
    	StepBuilder stepBuilder =  stepBuilderFactory.get("jobAStep3");
        SimpleStepBuilder<TableContent, String> simpleStepBuilder = stepBuilder.<TableContent, String> chunk(50);
        simpleStepBuilder.reader(step3Reader());
        simpleStepBuilder.processor(new DbToJsonProcessor<TableContent, String>());
        simpleStepBuilder.writer(new JsonFileItemWriter(csvFile));
        return simpleStepBuilder.build();
    }

    @Bean @Qualifier("jobA")
    public Job jobA() {

 		JobBuilder jobBuilder = jobBuilderFactory.get(jobName);
        jobBuilder.incrementer(new RunIdIncrementer());
        jobBuilder.preventRestart();
        jobBuilder.listener(jobFinishedListener);

        JobFlowBuilder jobFlowBuilder = jobBuilder.flow(step1());
        jobFlowBuilder.next(step2());
        jobFlowBuilder.next(step3());
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