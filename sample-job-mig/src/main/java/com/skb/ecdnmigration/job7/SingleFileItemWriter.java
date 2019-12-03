package com.skb.ecdnmigration.job7;

import java.io.FileOutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.skb.ecdnmigration.job.data.FilePackageInfo;

public class SingleFileItemWriter implements ItemWriter<FilePackageInfo> {
	private Logger logger = LoggerFactory.getLogger(SingleFileItemWriter.class);

	String file;
	
	public SingleFileItemWriter(String file) {
		this.file = file;
	}

	@Override
	public void write(List<? extends FilePackageInfo> items) throws Exception {
		FileOutputStream fosFile = new FileOutputStream(file, true);

		String line;
		logger.info(">>>> WRITE BEGIN CHUNK-COUNT=" + items.size());
		for(FilePackageInfo item : items) {
			line = item.toString();
			fosFile.write(line.getBytes());
		}
		logger.info("<<<< WRITE FINISHED");
		
		fosFile.close();
	}
}
