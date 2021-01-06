package com.skb.ecdnmigration.job8;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skb.ecdnmigration.job.data.TableContent;

public class DbToJsonProcessor<T1, T2> implements ItemProcessor<TableContent, String> {
	private Logger logger = LoggerFactory.getLogger(DbToJsonProcessor.class);

	@Override
	public String process(TableContent item) throws Exception {
		String json = "{}";
        try {
            ObjectMapper mapper = new ObjectMapper();
			json = mapper.writeValueAsString(item);
		} catch (IOException e) {
			e.printStackTrace();;
		}
        logger.info(json);
    	return json;
	}
}
