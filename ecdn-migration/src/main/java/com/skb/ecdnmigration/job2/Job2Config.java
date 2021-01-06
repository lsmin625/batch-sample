package com.skb.ecdnmigration.job2;

import java.util.Hashtable;

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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import com.sk.batch.lib.AdminConfig;
import com.sk.batch.lib.JobFinishedListener;
import com.sk.batch.lib.TriggerJobInfo;
import com.sk.batch.lib.TriggerJobList;
import com.skb.ecdnmigration.job.data.FileCaption;
import com.skb.ecdnmigration.job.data.VttFile;
import com.skb.ecdnmigration.job.data.VttSize;


@Configuration 
@Import(AdminConfig.class)
public class Job2Config {
	@Autowired private StepBuilderFactory stepBuilderFactory;
	@Autowired private JobBuilderFactory jobBuilderFactory;
	@Autowired private JobFinishedListener jobFinishedListener;
	@Autowired private TriggerJobList triggerJobList;

	@Value("${meta.admin-url}") private String adminUrl;
	@Value("${meta.callback-url}") private String callbackUrl;

	@Value("${job02.name}") private String jobName;
	@Value("${job02.desc}") private String jobDesc;
	@Value("${job02.mode}") private String jobMode;
	@Value("${job02.cron}") private String jobCron;

	@Value("file:${job02.file.input-vttlist}") private Resource fVttlist;
	@Value("file:${job02.file.input-caption}") private Resource fCaptionInput;
	@Value("${job02.file.output-caption}") private String fCaptionOutput;
	
	@Value("${data.limit}") private int dataLimit = 0;
	
	private Hashtable<String, String> vttTable = new Hashtable<String, String>();
	    
    private ItemReader<VttFile> step1Reader() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();  //default delimiter is comma(','); if any other, use .setDelimiter('')
        tokenizer.setDelimiter(",");
        tokenizer.setNames(new String[]{"id", "col2", "rw", "col4", "owner", "group", "size", "month", "day", "year", "vtt"});
        
        DefaultLineMapper<VttFile> lineMapper = new DefaultLineMapper<VttFile>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new VttFieldMapper());

        FlatFileItemReader<VttFile> reader = new FlatFileItemReader<VttFile>();
        reader.setLineMapper(lineMapper);
        reader.setResource(fVttlist);
//        reader.setLinesToSkip(1);
        return reader;
    }
 
    private Step step1() {
    	StepBuilder stepBuilder =  stepBuilderFactory.get("captionSizeJobStep1");
        SimpleStepBuilder<VttFile, VttSize> simpleStepBuilder = stepBuilder.<VttFile, VttSize>chunk(100);
        simpleStepBuilder.reader(step1Reader());
        simpleStepBuilder.processor(new FileToMemoryProcessor<VttFile, VttSize>());
        simpleStepBuilder.writer(new MemoryWriter(vttTable));
        return simpleStepBuilder.build();
    }

    private ItemReader<FileCaption> step2Reader() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();  //default delimiter is comma(','); if any other, use .setDelimiter('')
        tokenizer.setDelimiter(",");
        tokenizer.setNames(new String[]{"id", "cd", "name", "size", "date", "seq", "cid"});

        DefaultLineMapper<FileCaption> lineMapper = new DefaultLineMapper<FileCaption>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new CaptionFieldMapper());

        FlatFileItemReader<FileCaption> reader = new FlatFileItemReader<FileCaption>();
        reader.setLineMapper(lineMapper);
        reader.setResource(fCaptionInput);

        return reader;
    }
 
    private Step step2() {
    	StepBuilder stepBuilder =  stepBuilderFactory.get("captionSizeJobStep2");
        SimpleStepBuilder<FileCaption, FileCaption> simpleStepBuilder = stepBuilder.<FileCaption, FileCaption>chunk(100);
        simpleStepBuilder.reader(step2Reader());
        simpleStepBuilder.processor(new PassThroughItemProcessor<FileCaption>());
        simpleStepBuilder.writer(new CaptionFileWriter(vttTable, fCaptionOutput));
        return simpleStepBuilder.build();
    }

 	@Bean @Qualifier("captionSizeJob")
    public Job job02(@Qualifier("jobDataSource") DataSource jobDataSource) {

 		JobBuilder jobBuilder = jobBuilderFactory.get(jobName);
        jobBuilder.incrementer(new RunIdIncrementer());
        jobBuilder.preventRestart();
        jobBuilder.listener(jobFinishedListener);

        JobFlowBuilder jobFlowBuilder = jobBuilder.flow(step1());
        jobFlowBuilder.next(step2());
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