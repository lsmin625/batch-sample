package com.skb.ecdnmigration.job4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.LineMapper;

import com.skb.ecdnmigration.job.data.VttFile;

public class VttLineMapper implements LineMapper<VttFile>{
	private Logger logger = LoggerFactory.getLogger(VttLineMapper.class);
	
	int limit = 0;
	
	public VttLineMapper(int limit) {
		this.limit = limit;
	}
	
	@Override
	public VttFile mapLine(String line, int lineNumber) throws Exception {
		if(limit > 0 && lineNumber > limit) {
			return null;
		}
		
		String []cols = line.split("\\s+");
		VttFile vtt = new VttFile();
		vtt.setId(cols[0]);
		vtt.setCol2(cols[1]);
		vtt.setRw(cols[2]);
		vtt.setCol4(cols[3]);
		vtt.setOwner(cols[4]);
		vtt.setGroup(cols[5]);
		vtt.setSize(cols[6]);
		vtt.setMonth(cols[7]);
		vtt.setDay(cols[8]);
		vtt.setYear(cols[9]);
		vtt.setVtt(cols[10]);
		return vtt;
	}

}
