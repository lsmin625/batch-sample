package com.skb.ecdnmigration.job5;

import java.net.MalformedURLException;
import java.util.regex.Pattern;

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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.RegexLineTokenizer;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileUrlResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import com.sk.batch.lib.AdminConfig;
import com.sk.batch.lib.data.TriggerJobInfo;
import com.sk.batch.lib.data.TriggerJobList;
import com.sk.batch.lib.service.JobFinishedListener;
import com.skb.ecdnmigration.job.data.FileCaption;
import com.skb.ecdnmigration.job.data.FileList;
import com.skb.ecdnmigration.job.data.TableContent;
import com.skb.ecdnmigration.job.data.VttCsv;
import com.skb.ecdnmigration.job.data.VttFile;
import com.skb.ecdnmigration.job.data.VttSize;
import com.skb.ecdnmigration.job1.DbToCvsProcessor;
import com.skb.ecdnmigration.job1.Job1Config;
import com.skb.ecdnmigration.job1.MultiFileItemWriter;
import com.skb.ecdnmigration.job1.TableRowMapper;
import com.skb.ecdnmigration.job2.FileToMemoryProcessor;
import com.skb.ecdnmigration.job2.VttFieldMapper;


@Configuration 
@Import({AdminConfig.class, Job1Config.class})
public class Job5Config {
	private Logger logger = LoggerFactory.getLogger(Job5Config.class);

	@Autowired private StepBuilderFactory stepBuilderFactory;
	@Autowired private JobBuilderFactory jobBuilderFactory;
	@Autowired private JobFinishedListener jobFinishedListener;
	@Autowired private TriggerJobList triggerJobList;
	@Autowired @Qualifier("jobDataSource") private DataSource jobDataSource;

	@Value("${meta.admin-url}") private String adminUrl;
	@Value("${meta.callback-url}") private String callbackUrl;

	@Value("${job05.name}") private String jobName;
	@Value("${job05.desc}") private String jobDesc;
	@Value("${job05.mode}") private String jobMode;
	@Value("${job05.cron}") private String jobCron;

	@Value("${job05.file.dir-output}") private String dirOutput;

	@Value("${data.limit}") private int dataLimit = 0;

    private ItemReader<VttCsv> normalReader(String region) {
    	StringBuffer sql = new StringBuffer();
    	sql.append("SELECT * ");
    	sql.append("FROM tb_media_size ");
       	sql.append("WHERE region='" + region + "' ");
       	sql.append(" AND m4a_name IS NOT NULL ");
       	sql.append(" AND m4v_name IS NOT NULL ");
       	sql.append(" AND mp4_name IS NOT NULL ");
    	if(dataLimit > 0) {
        	sql.append("LIMIT " + dataLimit + " OFFSET 0");
    	}
    	
    	JdbcCursorItemReader<VttCsv> reader = new JdbcCursorItemReader<VttCsv>();
        reader.setDataSource(jobDataSource);
        reader.setRowMapper(new VttCsvRowMapper());
        reader.setSql(sql.toString());
        return reader;
    }

    private ItemReader<VttCsv> abnormalReader(String region) {
    	StringBuffer sql = new StringBuffer();
    	sql.append("SELECT * ");
    	sql.append("FROM tb_media_size ");
       	sql.append("WHERE region='" + region + "' ");
       	sql.append(" AND ( m4a_name IS NULL ");
       	sql.append(" OR m4v_name IS NULL ");
       	sql.append(" OR mp4_name IS NULL ) ");
    	if(dataLimit > 0) {
        	sql.append("LIMIT " + dataLimit + " OFFSET 0");
    	}
    	
    	JdbcCursorItemReader<VttCsv> reader = new JdbcCursorItemReader<VttCsv>();
        reader.setDataSource(jobDataSource);
        reader.setRowMapper(new VttCsvRowMapper());
        reader.setSql(sql.toString());
        return reader;
    }

    private Step stepNormalDong() {
    	StepBuilder stepBuilder =  stepBuilderFactory.get("stepNormalDong");
        SimpleStepBuilder<VttCsv, VttCsv> simpleStepBuilder = stepBuilder.<VttCsv, VttCsv> chunk(100);
        simpleStepBuilder.reader(normalReader("dong"));
        simpleStepBuilder.processor(new PassThroughItemProcessor<VttCsv>());
        simpleStepBuilder.writer(new CsvFileItemWriter(dirOutput + "dong-normal.csv"));
        return simpleStepBuilder.build();
    }

    private Step stepNormalGang() {
    	StepBuilder stepBuilder =  stepBuilderFactory.get("stepNormalGang");
        SimpleStepBuilder<VttCsv, VttCsv> simpleStepBuilder = stepBuilder.<VttCsv, VttCsv> chunk(100);
        simpleStepBuilder.reader(normalReader("gang"));
        simpleStepBuilder.processor(new PassThroughItemProcessor<VttCsv>());
        simpleStepBuilder.writer(new CsvFileItemWriter(dirOutput + "gang-normal.csv"));
        return simpleStepBuilder.build();
    }

    private Step stepAbnormalDong() {
    	StepBuilder stepBuilder =  stepBuilderFactory.get("stepAbnormalDong");
        SimpleStepBuilder<VttCsv, VttCsv> simpleStepBuilder = stepBuilder.<VttCsv, VttCsv> chunk(100);
        simpleStepBuilder.reader(abnormalReader("dong"));
        simpleStepBuilder.processor(new PassThroughItemProcessor<VttCsv>());
        simpleStepBuilder.writer(new CsvFileItemWriter(dirOutput + "dong-abnormal.csv"));
        return simpleStepBuilder.build();
    }

    private Step stepAbnormalGang() {
    	StepBuilder stepBuilder =  stepBuilderFactory.get("stepAbnormalGang");
        SimpleStepBuilder<VttCsv, VttCsv> simpleStepBuilder = stepBuilder.<VttCsv, VttCsv> chunk(100);
        simpleStepBuilder.reader(abnormalReader("gang"));
        simpleStepBuilder.processor(new PassThroughItemProcessor<VttCsv>());
        simpleStepBuilder.writer(new CsvFileItemWriter(dirOutput + "gang-abnormal.csv"));
        return simpleStepBuilder.build();
    }

 	@Bean @Qualifier("ecdnCsvWriter")
    public Job job05() {

 		JobBuilder jobBuilder = jobBuilderFactory.get(jobName);
        jobBuilder.incrementer(new RunIdIncrementer());
        jobBuilder.preventRestart();
        jobBuilder.listener(jobFinishedListener);

        JobFlowBuilder jobFlowBuilder = jobBuilder.flow(stepNormalDong());
        jobFlowBuilder.next(stepNormalGang());
        jobFlowBuilder.next(stepAbnormalDong());
        jobFlowBuilder.next(stepAbnormalGang());
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