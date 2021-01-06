package com.skb.ecdnmigration.job9;

import java.io.FileOutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

public class JsonFileItemWriter implements ItemWriter<String> {
	private Logger logger = LoggerFactory.getLogger(JsonFileItemWriter.class);
	String file;
	
	public JsonFileItemWriter(String file) {
		this.file = file;
	}

	@Override
	public void write(List<? extends String> items) throws Exception {
		FileOutputStream fosFile = new FileOutputStream(file, true);

		for(String item : items) {
			logger.info(item);
			fosFile.write(item.getBytes());
		}
		
		fosFile.close();
	}
}
