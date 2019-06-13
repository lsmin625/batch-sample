package com.skb.ecdnmigration.job4;

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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.RegexLineTokenizer;
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
import com.sk.batch.lib.JobFinishedListener;
import com.sk.batch.lib.TriggerJobInfo;
import com.sk.batch.lib.TriggerJobList;
import com.skb.ecdnmigration.job.data.VttFile;
import com.skb.ecdnmigration.job.data.VttSize;
import com.skb.ecdnmigration.job2.FileToMemoryProcessor;
import com.skb.ecdnmigration.job2.VttFieldMapper;


@Configuration 
@Import(AdminConfig.class)
public class Job4Config {
	private Logger logger = LoggerFactory.getLogger(Job4Config.class);

	@Autowired private StepBuilderFactory stepBuilderFactory;
	@Autowired private JobBuilderFactory jobBuilderFactory;
	@Autowired private JobFinishedListener jobFinishedListener;
	@Autowired private TriggerJobList triggerJobList;
	@Autowired private Environment env;

	@Value("${meta.admin-url}") private String adminUrl;
	@Value("${meta.callback-url}") private String callbackUrl;

	@Value("${job04.name}") private String jobName;
	@Value("${job04.desc}") private String jobDesc;
	@Value("${job04.mode}") private String jobMode;
	@Value("${job04.cron}") private String jobCron;

	@Value("${job04.file.dir-dong}") private String dirDong;
	@Value("${job04.file.dir-gang}") private String dirGang;

	@Value("${data.limit}") private int dataLimit = 0;

    private DataSource job4DataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("job04.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("job04.datasource.url"));
        dataSource.setUsername(env.getProperty("job04.datasource.username"));
        dataSource.setPassword(env.getProperty("job04.datasource.password"));
        return dataSource;
    }

    private NamedParameterJdbcTemplate job4NamedJdbcTemplate() {
       	NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(job4DataSource());
    	return jdbcTemplate;
    }

    private ItemReader<VttFile> stepReader(String fname) {
        FlatFileItemReader<VttFile> reader = new FlatFileItemReader<VttFile>();
        reader.setLineMapper(new VttLineMapper(dataLimit));

        try {
			reader.setResource(new FileUrlResource(fname));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        return reader;
    }

    private ItemWriter<VttSize> stepWriter(String key) {
    	StringBuffer sql = new StringBuffer();
    	sql.append("INSERT INTO media_size (region, media_id, ");
    	sql.append(" " + key + "_name, " + key + "_size ) ");
    	sql.append(" VALUES (?, ?, ?, ?)");
    	sql.append(" ON DUPLICATE KEY  UPDATE ");
    	sql.append(" region=?, media_id=?, ");
    	sql.append(" " + key + "_name=?, " + key + "_size=?;");

     	JdbcBatchItemWriter<VttSize> writer = new JdbcBatchItemWriter<VttSize>();
     	writer.setDataSource(job4DataSource());
  		writer.setJdbcTemplate(job4NamedJdbcTemplate());
     	writer.setSql(sql.toString());
     	writer.setItemPreparedStatementSetter(new VttSizePrepareStatementSetter());
     	return writer;
     }

    private Step step11() {
   	StepBuilder stepBuilder =  stepBuilderFactory.get("DongAudioSize");
       SimpleStepBuilder<VttFile, VttSize> simpleStepBuilder = stepBuilder.<VttFile, VttSize>chunk(100);
       simpleStepBuilder.reader(stepReader(dirDong + "file_list_dong_m4a.log"));
       simpleStepBuilder.processor(new FileToMemoryProcessor<VttFile, VttSize>("dong"));
       simpleStepBuilder.writer(stepWriter("m4a"));
       return simpleStepBuilder.build();
   }

    private Step step12() {
   	StepBuilder stepBuilder =  stepBuilderFactory.get("DongVideoSize");
       SimpleStepBuilder<VttFile, VttSize> simpleStepBuilder = stepBuilder.<VttFile, VttSize>chunk(100);
       simpleStepBuilder.reader(stepReader(dirDong + "file_list_dong_m4v.log"));
       simpleStepBuilder.processor(new FileToMemoryProcessor<VttFile, VttSize>("dong"));
       simpleStepBuilder.writer(stepWriter("m4v"));
       return simpleStepBuilder.build();
   }

    private Step step13() {
   	StepBuilder stepBuilder =  stepBuilderFactory.get("DongSourceSize");
       SimpleStepBuilder<VttFile, VttSize> simpleStepBuilder = stepBuilder.<VttFile, VttSize>chunk(100);
       simpleStepBuilder.reader(stepReader(dirDong + "file_list_dong_mp4.log"));
       simpleStepBuilder.processor(new FileToMemoryProcessor<VttFile, VttSize>("dong"));
       simpleStepBuilder.writer(stepWriter("mp4"));
       return simpleStepBuilder.build();
   }

    private Step step21() {
   	StepBuilder stepBuilder =  stepBuilderFactory.get("GangAudioSize");
       SimpleStepBuilder<VttFile, VttSize> simpleStepBuilder = stepBuilder.<VttFile, VttSize>chunk(100);
       simpleStepBuilder.reader(stepReader(dirGang + "file_list_gang_m4a.log"));
       simpleStepBuilder.processor(new FileToMemoryProcessor<VttFile, VttSize>("gang"));
       simpleStepBuilder.writer(stepWriter("m4a"));
       return simpleStepBuilder.build();
   }

    private Step step22() {
   	StepBuilder stepBuilder =  stepBuilderFactory.get("GangVideoSize");
       SimpleStepBuilder<VttFile, VttSize> simpleStepBuilder = stepBuilder.<VttFile, VttSize>chunk(100);
       simpleStepBuilder.reader(stepReader(dirGang + "file_list_gang_m4v.log"));
       simpleStepBuilder.processor(new FileToMemoryProcessor<VttFile, VttSize>("gang"));
       simpleStepBuilder.writer(stepWriter("m4v"));
       return simpleStepBuilder.build();
   }

    private Step step23() {
   	StepBuilder stepBuilder =  stepBuilderFactory.get("GangSourceSize");
       SimpleStepBuilder<VttFile, VttSize> simpleStepBuilder = stepBuilder.<VttFile, VttSize>chunk(100);
       simpleStepBuilder.reader(stepReader(dirGang + "file_list_gang_mp4.log"));
       simpleStepBuilder.processor(new FileToMemoryProcessor<VttFile, VttSize>("gang"));
       simpleStepBuilder.writer(stepWriter("mp4"));
       return simpleStepBuilder.build();
   }

 	@Bean @Qualifier("ecdnCheckAllJob")
    public Job job04() {

 		JobBuilder jobBuilder = jobBuilderFactory.get(jobName);
        jobBuilder.incrementer(new RunIdIncrementer());
        jobBuilder.preventRestart();
        jobBuilder.listener(jobFinishedListener);

        JobFlowBuilder jobFlowBuilder = jobBuilder.flow(step11());
        jobFlowBuilder.next(step12());
        jobFlowBuilder.next(step13());
        jobFlowBuilder.next(step21());
        jobFlowBuilder.next(step22());
        jobFlowBuilder.next(step23());
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