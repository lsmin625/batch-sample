package com.skb.ecdnmigration.jobA;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.skb.ecdnmigration.job.data.TableJobId;

public class TableJobIdMapper implements RowMapper<TableJobId> {
	@Override
	public TableJobId mapRow(ResultSet rs, int rowNum) throws SQLException {
		TableJobId data = new TableJobId();
		data.setJobId(rs.getInt("job_id"));
		data.setContentSeq(rs.getInt("content_seq"));

		return data;
	}
}
