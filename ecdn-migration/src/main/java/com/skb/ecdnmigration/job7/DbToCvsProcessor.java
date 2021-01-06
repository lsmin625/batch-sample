package com.skb.ecdnmigration.job7;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.skb.ecdnmigration.job.data.FilePackageInfo;
import com.skb.ecdnmigration.job.data.JsonPackageInfo;
import com.skb.ecdnmigration.job.data.TableContent;

public class DbToCvsProcessor<T1, T2> implements ItemProcessor<TableContent, FilePackageInfo> {
	private Logger logger = LoggerFactory.getLogger(DbToCvsProcessor.class);
	
	@Override
	public FilePackageInfo process(TableContent item) throws Exception {
		FilePackageInfo list = new FilePackageInfo();

		JsonPackageInfo packInfo = item.getPackageInfo();
		list.setContentId(packInfo.getContent_id());
		list.setContentPath(packInfo.getContent_path());
		list.setContentName(packInfo.getContent_name());
		list.setMediaId(packInfo.getMedia_id());
		list.setCaptionPath(packInfo.getCaption_path());
		list.setCaptionTypes(packInfo.getCaption_types());

		logger.info(">>>> PROCESSOR]" + list.toString());
		return list;
	}
	
}
