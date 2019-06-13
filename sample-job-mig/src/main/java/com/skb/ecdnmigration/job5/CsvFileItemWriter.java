package com.skb.ecdnmigration.job5;

import java.io.FileOutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.skb.ecdnmigration.job.data.VttCsv;

public class CsvFileItemWriter implements ItemWriter<VttCsv> {
	private Logger logger = LoggerFactory.getLogger(CsvFileItemWriter.class);

	String file;
	
	public CsvFileItemWriter(String file) {
		this.file = file;
	}

	@Override
	public void write(List<? extends VttCsv> items) throws Exception {
		FileOutputStream fos = new FileOutputStream(file, true);

		logger.info(">>>> WRITE BEGIN LIST-COUNT=" + items.size());
		for(VttCsv item : items) {
			fos.write(item.toString().getBytes());
		}
		logger.info("<<<< WRITE FINISHED");
		
		fos.close();
	}
	
	

}
