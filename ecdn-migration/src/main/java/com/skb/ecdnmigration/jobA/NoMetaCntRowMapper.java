package com.skb.ecdnmigration.jobA;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skb.ecdnmigration.job.data.JsonMetaInfo;
import com.skb.ecdnmigration.job.data.JsonPackageInfo;
import com.skb.ecdnmigration.job.data.TableContent;

public class NoMetaCntRowMapper implements RowMapper<TableContent> {
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public TableContent mapRow(ResultSet rs, int rowNum) throws SQLException {
		String packInfo = rs.getString("package_info");
		if(packInfo.startsWith("\"")) {
			int len = packInfo.length();
			packInfo = packInfo.substring(1, len-1);
		}

		JsonPackageInfo jsonPackage = null;
		JsonMetaInfo jsonMeta = null;
		try {
			mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
			jsonPackage = mapper.readValue(StringEscapeUtils.unescapeJson(packInfo), JsonPackageInfo.class);
	    } catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		TableContent data = new TableContent();
		data.setContentSeq(rs.getInt("content_seq"));
		data.setMediaId(rs.getString("media_id"));
		String cid = rs.getString("cid");
		int idx = cid.indexOf("{");
		if(idx > 0) {
			cid = cid.substring(idx);
		}
		data.setCid(cid);
		data.setPackageInfo(jsonPackage);
		data.setMetaInfo(jsonMeta);
		data.setRegisterDate(rs.getDate("register_date"));

		data.setStatus(rs.getString("status"));
		data.setStatusCode(rs.getInt("status_code"));
		data.setResultMessage(rs.getString("result_message"));
		
		return data;
	}
}
