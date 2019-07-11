package com.skb.ecdnmigration.job1;

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
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import com.sk.batch.lib.AdminConfig;
import com.sk.batch.lib.JobFinishedListener;
import com.sk.batch.lib.TriggerJobInfo;
import com.sk.batch.lib.TriggerJobList;
import com.skb.ecdnmigration.job.data.TableContent;
import com.skb.ecdnmigration.job.data.FileList;


@Configuration 
@Import(AdminConfig.class)
public class Job1Config {
	@Autowired private Environment env;
	@Autowired private StepBuilderFactory stepBuilderFactory;
	@Autowired private JobBuilderFactory jobBuilderFactory;
	@Autowired private JobFinishedListener jobFinishedListener;
	@Autowired private TriggerJobList triggerJobList;

	@Value("${meta.admin-url}") private String adminUrl;
	@Value("${meta.callback-url}") private String callbackUrl;

	@Value("${job01.name}") private String jobName;
	@Value("${job01.desc}") private String jobDesc;
	@Value("${job01.mode}") private String jobMode;
	@Value("${job01.cron}") private String jobCron;

	@Value("${job01.file.output-common}") private String fCommon;
	@Value("${job01.file.output-video}") private String fVideo;
	@Value("${job01.file.output-audio}") private String fAudio;
	@Value("${job01.file.output-caption}") private String fCaption;
	
	@Value("${data.limit}") private int dataLimit = 0;
	    
    @Bean @Qualifier("migDataSource")
    public DataSource migDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("mig.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("mig.datasource.url"));
        dataSource.setUsername(env.getProperty("mig.datasource.username"));
        dataSource.setPassword(env.getProperty("mig.datasource.password"));
        return dataSource;
    }
    
    @Bean @Qualifier("migJdbcTemplate")
    public NamedParameterJdbcTemplate migJdbcTemplate(@Qualifier("migDataSource") DataSource dataSource) {
       	NamedParameterJdbcTemplate jobJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    	return jobJdbcTemplate;
    }

    @Bean @Qualifier("jobDataSource")
    public DataSource jobDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("job.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("job.datasource.url"));
        dataSource.setUsername(env.getProperty("job.datasource.username"));
        dataSource.setPassword(env.getProperty("job.datasource.password"));
        return dataSource;
    }
    
    @Bean @Qualifier("jobJdbcTemplate")
    public NamedParameterJdbcTemplate jobJdbcTemplate(@Qualifier("jobDataSource") DataSource dataSource) {
       	NamedParameterJdbcTemplate jobJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    	return jobJdbcTemplate;
    }

    private ItemReader<TableContent> step1Reader(DataSource dataSource) {
    	StringBuffer sql = new StringBuffer();
    	sql.append("SELECT tb_content.content_seq, media_id, cid, package_info, meta_info, tb_content.register_date ");
    	sql.append("FROM tb_content, tb_content_meta ");
       	sql.append("WHERE tb_content.status_code = 7 ");
       	sql.append(" AND tb_content.content_seq = tb_content_meta.content_seq ");
       	sql.append(" AND tb_content_meta.file_path = 'stb_info' ");
//       	sql.append(" AND tb_content_meta.modify_date >= '2019-07-08 00:00:00' ");
    	if(dataLimit > 0) {
        	sql.append("LIMIT " + dataLimit + " OFFSET 0");
    	}
    	
    	JdbcCursorItemReader<TableContent> reader = new JdbcCursorItemReader<TableContent>();
        reader.setDataSource(dataSource);
        reader.setRowMapper(new TableRowMapper());
        reader.setSql(sql.toString());
        return reader;
    }
 
    private Step step1(DataSource dataSource) {
    	StepBuilder stepBuilder =  stepBuilderFactory.get("ecdnNcmsStep1");
        SimpleStepBuilder<TableContent, FileList> simpleStepBuilder = stepBuilder.<TableContent, FileList> chunk(50);
        simpleStepBuilder.reader(step1Reader(dataSource));
        simpleStepBuilder.processor(new DbToCvsProcessor<TableContent, FileList>());
        simpleStepBuilder.writer(new MultiFileItemWriter(fCommon, fVideo, fAudio, fCaption));
        return simpleStepBuilder.build();
    }

 	@Bean @Qualifier("ecdnNcmsJob")
    public Job job01(@Qualifier("migDataSource") DataSource dataSource) {

 		JobBuilder jobBuilder = jobBuilderFactory.get(jobName);
        jobBuilder.incrementer(new RunIdIncrementer());
        jobBuilder.preventRestart();
        jobBuilder.listener(jobFinishedListener);

        JobFlowBuilder jobFlowBuilder = jobBuilder.flow(step1(dataSource));
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