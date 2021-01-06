package com.skb.ecdnmigration.job4;

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
import com.skb.ecdnmigration.job.data.VttContent;
import com.skb.ecdnmigration.job.data.VttCsv;

public class VttContentRowMapper implements RowMapper<VttContent> {
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public VttContent mapRow(ResultSet rs, int rowNum) throws SQLException {
		VttContent vtt = new VttContent();
		vtt.setMediaId(rs.getString("media_id"));
		vtt.setCid(rs.getString("cid"));
		
		String packInfo = rs.getString("package_info");
		if(packInfo.startsWith("\"")) {
			int len = packInfo.length();
			packInfo = packInfo.substring(1, len-1);
		}
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

		vtt.setContentName(jsonPackage.getContent_name());
		
		return vtt;
	}

}
