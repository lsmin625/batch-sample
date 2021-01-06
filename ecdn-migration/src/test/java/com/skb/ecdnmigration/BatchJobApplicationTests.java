package com.skb.ecdnmigration;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skb.ecdnmigration.job.data.JsonPackageInfo;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BatchJobApplicationTests {
	private Logger logger = LoggerFactory.getLogger(BatchJobApplicationTests.class);

	@Test
	public void contextLoads() {
		String msg = "{\"content_id\": \"\\ufeff{5EECED34-EFDF-4C13-B0AC-753836CF0608}\", \"content_name\": \"\\uac70\\uce68\\uc5c6\\uc774 \\ud558\\uc774\\ud0a5\", \"content_path\": \"/ontv/10400/T30353_156_181018_1646.ts\", \"caption_path\": null, \"caption_types\": null, \"cdn_code\": \"6\", \"video_track_sub_type\": \"10\", \"video_track_code\": \"\", \"media_id\": \"CD1000000001\", \"migration_data\": {\"media_id\": \"CD1000000001\", \"directory_path\": \"/vod/01/CD1000000001\", \"master_m3u8_name\": \"CD1000000001_20181128181516\", \"segment_duration\": 4, \"fragment_duration\": 0.5, \"clear_duration\": 0, \"track_info\": [{\"input_file_path\": \"/ontv/10400/T30353_156_181018_1646.ts\", \"output_name\": \"CD1000000001_VD_TVSDM_20181128181516\", \"track_id\": 0, \"track_type\": \"VD\", \"track_code\": \"\", \"track_sub_type\": \"10\", \"track_name\": \"720p\", \"clean_path\": \"/ontv/10400\", \"clean_name\": \"CD1000000001_20181128181516.mp4\"}, {\"input_file_path\": \"/ontv/10400/T30353_156_181018_1646.ts\", \"output_name\": \"CD1000000001_AD_UN00_20181128181516\", \"track_id\": 1, \"track_type\": \"AD\", \"track_code\": \"00\", \"track_sub_type\": \"00\", \"track_name\": \"UND\", \"clean_path\": \"/ontv/10400\", \"clean_name\": \"CD1000000001_20181128181516.mp4\"}]}, \"data_version\": \"20181128181516\", \"duration\": 1717}";

		logger.info(msg);
		String msg2 = decode(msg);
		logger.info(msg2);
		ObjectMapper mapper = new ObjectMapper();
		JsonPackageInfo jsonPackage = null;
		try {
			mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
			jsonPackage = mapper.readValue(msg2, JsonPackageInfo.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info(jsonPackage.toString());
	}

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

}
