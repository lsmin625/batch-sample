package com.skb.ecdnmigration.job4;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

public class MemoryItemWriter implements ItemWriter<Object> {
	private Logger logger = LoggerFactory.getLogger(MemoryItemWriter.class);

	String check;
	
	public MemoryItemWriter(String check) {
		this.check = check;
	}

	@Override
	public void write(List<? extends Object> items) throws Exception {

	}
}
