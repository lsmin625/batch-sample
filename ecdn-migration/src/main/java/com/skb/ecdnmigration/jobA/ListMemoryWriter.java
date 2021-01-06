package com.skb.ecdnmigration.jobA;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.skb.ecdnmigration.job.data.TableJobId;

public class ListMemoryWriter implements ItemWriter<TableJobId> {
	List<TableJobId> jobIdList;
	
	public ListMemoryWriter(List<TableJobId> jobIdList) {
		this.jobIdList = jobIdList;
	}

	@Override
	public void write(List<? extends TableJobId> items) throws Exception {
		for(TableJobId item : items) {
			jobIdList.add(item);
		}
	}
}
