package com.skb.ecdnmigration.jobA;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementSetter;
import com.skb.ecdnmigration.job.data.TableJobId;


public class JobIdPrepareStatementSetter implements PreparedStatementSetter {

	private List<TableJobId> jobIdList;
	private int index = 0;

	public JobIdPrepareStatementSetter(List<TableJobId> jobIdList) {
		this.jobIdList = jobIdList;
	}

	@Override
	public void setValues(PreparedStatement ps) throws SQLException {
		TableJobId item = jobIdList.get(index++);
        ps.setInt(1, item.getJobId());
        ps.setInt(2, item.getContentSeq());
	}
}
