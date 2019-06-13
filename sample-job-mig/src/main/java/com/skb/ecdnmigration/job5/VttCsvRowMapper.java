package com.skb.ecdnmigration.job5;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.skb.ecdnmigration.job.data.VttCsv;

public class VttCsvRowMapper implements RowMapper<VttCsv> {

	@Override
	public VttCsv mapRow(ResultSet rs, int rowNum) throws SQLException {
		VttCsv csv = new VttCsv();
		
		csv.setRegion(rs.getString("region"));
		csv.setMediaId(rs.getString("media_id"));
		csv.setM4aName(rs.getString("m4a_name"));
		csv.setM4aSize(rs.getLong("m4a_size"));
		csv.setM4vName(rs.getString("m4v_name"));
		csv.setM4vSize(rs.getLong("m4v_size"));
		csv.setMp4Name(rs.getString("mp4_name"));
		csv.setMp4Size(rs.getLong("mp4_size"));
		
		return csv;
	}

}
