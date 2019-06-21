package com.skb.ecdnmigration.job6;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skb.ecdnmigration.job.data.JsonMetaInfo;
import com.skb.ecdnmigration.job.data.JsonPackageInfo;
import com.skb.ecdnmigration.job.data.TableContent;

public class ContentRowMapper implements RowMapper<TableContent> {
	private Logger logger = LoggerFactory.getLogger(ContentRowMapper.class);
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public TableContent mapRow(ResultSet rs, int rowNum) throws SQLException {
		String packInfo = rs.getString("package_info");
		if(packInfo.startsWith("\"")) {
			int len = packInfo.length();
			packInfo = packInfo.substring(1, len-1);
		}
		logger.info(">>>>PACK] " + packInfo);

		JsonPackageInfo jsonPackage = null;
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
		data.setRegisterDate(rs.getDate("register_date"));

		logger.info(">>>>JSON-TABLE] " + data.toString());
		return data;
	}
}
