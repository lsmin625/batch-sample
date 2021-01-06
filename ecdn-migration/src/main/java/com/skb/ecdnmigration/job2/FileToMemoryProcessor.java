package com.skb.ecdnmigration.job2;

import org.springframework.batch.item.ItemProcessor;

import com.skb.ecdnmigration.job.data.VttFile;
import com.skb.ecdnmigration.job.data.VttSize;

public class FileToMemoryProcessor<T1, T2> implements ItemProcessor<VttFile, VttSize> {
	private String region = null;
	
	public FileToMemoryProcessor() {
		//do nothing
	}

	public FileToMemoryProcessor(String region) {
		this.region = region;
	}

	@Override
	public VttSize process(VttFile item) throws Exception {

        VttSize vtt = new VttSize();
        vtt.setRegion(region);
		vtt.setSize(item.getSize());
        
		String []cols = item.getVtt().split("/");
        vtt.setMediaId(cols[cols.length - 2]);
        vtt.setName(cols[cols.length - 1]);
 
		return vtt;
	}

}
