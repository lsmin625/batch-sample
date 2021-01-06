package com.skb.ecdnmigration.job7;

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

public class TableRowMapper implements RowMapper<TableContent> {
	private Logger logger = LoggerFactory.getLogger(TableRowMapper.class);
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public TableContent mapRow(ResultSet rs, int rowNum) throws SQLException {
		String packInfo = rs.getString("package_info");
//		String metaInfo = rs.getString("meta_info");
		if(packInfo.startsWith("\"")) {
			int len = packInfo.length();
			packInfo = packInfo.substring(1, len-1);
		}
//		metaInfo.replaceAll("\"", "");
		logger.info(">>>>ROW-MAPPER.PACK] " + packInfo);
//		logger.info(">>>>META] " + metaInfo);

		JsonPackageInfo jsonPackage = null;
//		JsonMetaInfo jsonMeta = null;
		try {
			mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
			jsonPackage = mapper.readValue(StringEscapeUtils.unescapeJson(packInfo), JsonPackageInfo.class);
//			jsonMeta = mapper.readValue(StringEscapeUtils.unescapeJson(metaInfo), JsonMetaInfo.class);
	    } catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		logger.info(">>>>JSON-PACK] " + jsonPackage.toString());
//		logger.info(">>>>JSON-META] " + jsonMeta.toString());

		TableContent data = new TableContent();
		data.setContentSeq(rs.getInt("tb_content.content_seq"));
		data.setMediaId(rs.getString("media_id"));
		String cid = rs.getString("cid");
		int idx = cid.indexOf("{");
		if(idx > 0) {
			cid = cid.substring(idx);
		}
		data.setCid(cid);
		data.setPackageInfo(jsonPackage);
//		data.setMetaInfo(jsonMeta);
		data.setRegisterDate(rs.getDate("tb_content.register_date"));

		logger.info(">>>>TABLE-CONTENT] " + data.toString());
		
		return data;
	}

	/*	
	private static String decode(String uni) {
		StringBuffer buff = new StringBuffer();
		int index = 0;
		while((index = uni.indexOf("\\u")) >= 0) {
			buff.append(uni.substring(0, index));
			buff.append(String.valueOf((char) Integer.parseInt(uni.substring(index + 2, index + 6), 16)));
			uni = uni.substring(index + 6);
		}
		buff.append(uni);
		return buff.toString();
	}
	*/
}
