package com.skb.ecdnmigration.job2;

import org.springframework.batch.item.ItemProcessor;

import com.skb.ecdnmigration.job.data.VttFile;
import com.skb.ecdnmigration.job.data.VttSize;

public class FileToMemoryProcessor<T1, T2> implements ItemProcessor<VttFile, VttSize> {

	@Override
	public VttSize process(VttFile item) throws Exception {
       int index = item.getVtt().lastIndexOf("/") + 1;

        VttSize vtt = new VttSize();
        vtt.setName(item.getVtt().substring(index));
		vtt.setSize(item.getSize());
 
		return vtt;
	}

}
