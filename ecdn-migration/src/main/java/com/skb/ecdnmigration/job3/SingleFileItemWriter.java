package com.skb.ecdnmigration.job3;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.skb.ecdnmigration.job.data.FileCheck;

public class SingleFileItemWriter implements ItemWriter<FileCheck> {
	private Logger logger = LoggerFactory.getLogger(SingleFileItemWriter.class);

	String check;
	
	public SingleFileItemWriter(String check) {
		this.check = check;
	}

	@Override
	public void write(List<? extends FileCheck> items) throws Exception {
		FileOutputStream fosCheck = new FileOutputStream(check + ".csv", true);
		FileOutputStream fosError = new FileOutputStream(check + ".err", true);

		String line;
		logger.info(">>>> WRITE BEGIN CHUNK-COUNT=" + items.size());
		for(FileCheck item : items) {
			if(item.getContentName() == null || item.getContentName().equals("")) {
				line = item.getMediaId();
				fosError.write(line.getBytes());
			}
			else {
				line = item.toString();
				fosCheck.write(line.getBytes());
			}
		}
		logger.info("<<<< WRITE FINISHED");
		
		fosCheck.close();
		fosError.close();
	}
	
	

}
