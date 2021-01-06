package com.skb.ecdnmigration.job2;

import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.skb.ecdnmigration.job.data.VttSize;

public class MemoryWriter implements ItemWriter<VttSize>{
	private Logger logger = LoggerFactory.getLogger(MemoryWriter.class);
	
	private Hashtable<String, String> vttTable;
	
	public MemoryWriter(Hashtable<String, String> table) {
		this.vttTable = table;
	}

	@Override
	public void write(List<? extends VttSize> items) throws Exception {
		for(VttSize item : items) {
			vttTable.put(item.getName(), item.getSize());
			logger.info(">>>> MEMORY WRITER] " + item.toString());
		}
	}

}
