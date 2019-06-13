package com.skb.ecdnmigration.job4;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import com.skb.ecdnmigration.job.data.VttContent;

public class VttContentPrepareStatementSetter implements ItemPreparedStatementSetter<VttContent> {

	@Override
	public void setValues(VttContent item, PreparedStatement ps) throws SQLException {
        ps.setString(1, item.getCid());
        ps.setString(2, item.getContentName());
        ps.setString(3, item.getMediaId());
 	}
}
