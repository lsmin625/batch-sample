package com.acme.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan(basePackages= {"com.sk.batch.lib", "com.acme.batch"})
@EnableBatchProcessing
@EnableScheduling
@SpringBootApplication
public class SampleJobShellApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleJobShellApplication.class, args);
	}

}
