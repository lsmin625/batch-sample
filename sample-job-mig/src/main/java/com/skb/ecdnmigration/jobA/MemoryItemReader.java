package com.skb.ecdnmigration.jobA;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.skb.ecdnmigration.job.data.TableContent;
import com.skb.ecdnmigration.job.data.TableJobId;

public class MemoryItemReader implements ItemReader<TableContent> {
	private Logger logger = LoggerFactory.getLogger(MemoryItemReader.class);

	List<TableJobId> jobIdList;
	NamedParameterJdbcTemplate migJdbcTemplate;
	
	public MemoryItemReader(List<TableJobId> jobIdList, NamedParameterJdbcTemplate migJdbcTemplate) {
		this.jobIdList = jobIdList;
		this.migJdbcTemplate = migJdbcTemplate;
	}


	@Override
	public TableContent read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if(jobIdList.size() > 0) {
			TableJobId jobId = jobIdList.remove(0);
			
	    	StringBuffer sql = new StringBuffer();
	    	sql.append("SELECT C.content_seq, C.media_id, C.cid, C.package_info, C.register_date, C.status, C.status_code, T.result_message ");
	    	sql.append(" FROM tb_content as C, tb_task T ");
	    	sql.append(" WHERE T.job_id = " + jobId.getJobId() + " and C.content_seq = " + jobId.getContentSeq());
	    	List<TableContent> list = migJdbcTemplate.query(sql.toString(), new NoMetaCntRowMapper());
	    	if(list.size() > 0) {
		    	for(TableContent item :  list) {
		    		if(item.getResultMessage().startsWith("download")) {
		    			return item;
		    		}
		    	}
		    	return list.get(0);
	    	}
	    	else {
		    	logger.info("NO-DATA: " + sql.toString());

		    	sql.setLength(0);
		    	sql.append("SELECT C.content_seq, C.media_id, C.cid, C.package_info, C.register_date, C.status, C.status_code ");
		    	sql.append(" FROM tb_content as C");
		    	sql.append(" WHERE C.content_seq = " + jobId.getContentSeq());
		    	list = migJdbcTemplate.query(sql.toString(), new NoMetaCntRowMapper());
		    	return list.get(0);
	    	}
		}
		else {
			return null;
		}
	}
}
