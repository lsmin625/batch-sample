package com.skb.ecdnmigration.job2;

import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.skb.ecdnmigration.job.data.FileCaption;

public class CaptionFileWriter implements ItemWriter<FileCaption> {
	private Logger logger = LoggerFactory.getLogger(CaptionFileWriter.class);

	private Hashtable<String, String> vttTable;
	private String fileOutput;
	
	public CaptionFileWriter(Hashtable<String, String> table, String output) {
		this.vttTable = table;
		this.fileOutput = output;
	}

	@Override
	public void write(List<? extends FileCaption> items) throws Exception {
		FileOutputStream fosCaptionSized = new FileOutputStream(fileOutput + "-sized.csv", true);
		FileOutputStream fosCaptionMissed = new FileOutputStream(fileOutput + "-missed.csv", true);

		String line;
		for(FileCaption item : items) {
			String size = vttTable.get(item.getFileNn());
			if(size != null) {
				item.setFileByteSz(size);
				line = item.toString();
				fosCaptionSized.write(line.getBytes());
				logger.info(">>>> CAPTION WRITER FOUND SIZE] " + item.toString());
			}
			else {
				line = item.toString();
				fosCaptionMissed.write(line.getBytes());
				logger.info("@@@@ CAPTION WRITER NO SIZE] " + item.toString());
			}
		}
		
		fosCaptionSized.close();
		fosCaptionMissed.close();
	}
}