package com.sk.batch.samplejobbean;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan(basePackages= {"com.sk.batch.lib", "com.sk.batch.samplejobbean"})
@EnableBatchProcessing
@EnableScheduling
@SpringBootApplication
public class BatchJobApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchJobApplication.class, args);
	}

}
