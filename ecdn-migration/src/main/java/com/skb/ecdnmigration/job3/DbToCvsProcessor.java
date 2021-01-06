package com.skb.ecdnmigration.job3;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.skb.ecdnmigration.job.data.FileCheck;
import com.skb.ecdnmigration.job.data.JsonMigrationData;
import com.skb.ecdnmigration.job.data.JsonPackageInfo;
import com.skb.ecdnmigration.job.data.JsonTrackInfo;
import com.skb.ecdnmigration.job.data.TableContent;

public class DbToCvsProcessor<T1, T2> implements ItemProcessor<TableContent, FileCheck> {
	private Logger logger = LoggerFactory.getLogger(DbToCvsProcessor.class);
	
	@Override
	public FileCheck process(TableContent item) throws Exception {
		FileCheck check = new FileCheck();
		check.setMediaId(item.getMediaId());
		check.setContentId(item.getCid());

		JsonPackageInfo packInfo = item.getPackageInfo();
		JsonMigrationData migData = packInfo.getMigration_data();
		if(migData == null) {
			logger.info("@@@@ NO MIGDATA] MEDIA-ID=" + item.getMediaId() + ", " + packInfo.toString());
			return check;
		}
		List<JsonTrackInfo> tracksArray = migData.getTrack_info();

		check.setContentName(packInfo.getContent_name());
		check.setContentPath(getPath(packInfo.getContent_path()));
		check.setContentFile(getFile(packInfo.getContent_path()));

		//to set Video list
		List<JsonTrackInfo> trackList = getTrackList(tracksArray, "VD");
		for(JsonTrackInfo track : trackList) {
			check.setCleanName(track.getClean_name());
		}
	
		return check;
	}
	
	private static List<JsonTrackInfo> getTrackList(List<JsonTrackInfo> tracksArray, String type) {
		ArrayList<JsonTrackInfo> list = new ArrayList<JsonTrackInfo>();
		for(JsonTrackInfo track : tracksArray) {
			if(type.equalsIgnoreCase(track.getTrack_type())) {
				list.add(track);
			}
		}
		return list;
	}
	
	private static String getFile(String path) {
		int index = path.lastIndexOf('/');
		return path.substring(index + 1);
	}

	private static String getPath(String path) {
		int index = path.lastIndexOf('/');
		return path.substring(0, index);
	}
}
