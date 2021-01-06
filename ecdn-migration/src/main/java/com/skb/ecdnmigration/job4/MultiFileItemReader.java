package com.skb.ecdnmigration.job4;

import java.io.BufferedReader;
import java.io.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class MultiFileItemReader implements ItemReader<Object> {
	private Logger logger = LoggerFactory.getLogger(MultiFileItemReader.class);

	String dong;
	String gang;
	
	public MultiFileItemReader(String dong, String gang) {
		this.dong = dong;
		this.gang = gang;
	}


	@Override
	public Object read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		BufferedReader reader = new BufferedReader(new FileReader(dong + "file_list_dong_m4a.log"));
		String line;
		while ((line = reader.readLine()) != null) {
			String []cols = line.split("\\s+");
			StringBuffer buff = new StringBuffer();
			for(int i = 0; i < cols.length; i++) {
				buff.append("[" + i + "=" + cols[i] + "]");
			}
			logger.info(buff.toString());
		}
		reader.close();
		
		return new Object();
	}
}
