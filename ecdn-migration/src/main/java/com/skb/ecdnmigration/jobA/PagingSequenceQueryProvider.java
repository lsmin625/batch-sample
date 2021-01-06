package com.skb.ecdnmigration.jobA;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;

public class PagingSequenceQueryProvider implements PagingQueryProvider {

	@Override
	public void init(DataSource dataSource) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String generateFirstPageQuery(int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateRemainingPagesQuery(int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateJumpToItemQuery(int itemIndex, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getParameterCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isUsingNamedParameters() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, Order> getSortKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSortKeyPlaceHolder(String keyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Order> getSortKeysWithoutAliases() {
		// TODO Auto-generated method stub
		return null;
	}

}
