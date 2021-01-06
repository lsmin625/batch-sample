package com.skb.ecdnmigration.job4;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import com.skb.ecdnmigration.job.data.VttSize;

public class VttSizePrepareStatementSetter implements ItemPreparedStatementSetter<VttSize> {

	@Override
	public void setValues(VttSize item, PreparedStatement ps) throws SQLException {
        ps.setString(1, item.getRegion());
        ps.setString(2, item.getMediaId());
        ps.setString(3, item.getName());
        ps.setLong(4, Long.parseLong(item.getSize()));
        ps.setString(5, item.getRegion());
        ps.setString(6, item.getMediaId());
        ps.setString(7, item.getName());
        ps.setLong(8, Long.parseLong(item.getSize()));
	}
}
