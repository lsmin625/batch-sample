package com.skb.ecdnmigration;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan(basePackages= {"com.sk.batch.lib", "com.skb.ecdnmigration"})
@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
public class BatchJobApplication {
	public static void main(String[] args) {
		SpringApplication.run(BatchJobApplication.class, args);
	}
}
