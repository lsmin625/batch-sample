package com.skb.ecdnmigration.jobA;

import java.util.List;

import org.springframework.batch.item.ItemProcessor;

import com.skb.ecdnmigration.job.data.JsonMetaAudio;
import com.skb.ecdnmigration.job.data.JsonMetaInfo;
import com.skb.ecdnmigration.job.data.JsonMetaMaster;
import com.skb.ecdnmigration.job.data.JsonMetaSubtitle;
import com.skb.ecdnmigration.job.data.JsonMetaVideo;
import com.skb.ecdnmigration.job.data.JsonMigrationData;
import com.skb.ecdnmigration.job.data.JsonPackageInfo;
import com.skb.ecdnmigration.job.data.JsonTrackInfo;
import com.skb.ecdnmigration.job.data.TableContent;

public class DbToJsonProcessor<T1, T2> implements ItemProcessor<TableContent, String> {
	@Override
	public String process(TableContent item) throws Exception {
		StringBuffer buff = new StringBuffer();
		JsonPackageInfo pack = item.getPackageInfo();
		JsonMetaInfo meta = item.getMetaInfo();
		
		buff.append(item.getMediaId() + ",");
		buff.append(item.getCid() + ",");
		buff.append(item.getStatus() + ",");
		buff.append(item.getStatusCode() + ",");
		if(pack != null) {
			buff.append(pack.getContent_name() + ",");
			buff.append(pack.getContent_path() + ",");
			buff.append(pack.getCaption_path() + ",");
			
			JsonMigrationData mig = pack.getMigration_data();
			if(mig != null) {
				List<JsonTrackInfo> track = pack.getMigration_data().getTrack_info();
				if(track != null) {
					buff.append(track.get(0).getClean_path() + ",");
					buff.append(track.get(0).getClean_name() + ",");
				}
			}
			else {
				buff.append(",,");
			}
		}
		else {
			buff.append(",,,,,");
		}
		
		if(meta != null) {
			JsonMetaMaster master = meta.getMaster();
			if(master != null) {
				buff.append(master.getPath() + ",");
			}
			else {
				buff.append(",");
			}

			List<JsonMetaAudio> audio = meta.getAudio();
			if(audio != null && audio.size() > 0) {
				buff.append(audio.get(0).getPath() + ",");
				buff.append(audio.get(0).getFilesize() + ",");
			}
			else {
				buff.append(",,");
			}

			List<JsonMetaVideo> video = meta.getVideo();
			if(video != null && video.size() > 0) {
				buff.append(video.get(0).getPath() + ",");
				buff.append(video.get(0).getFilesize() + ",");
			}
			else {
				buff.append(",,");
			}

			List<JsonMetaSubtitle> subtitle = meta.getSubtitle();
			if(subtitle != null && subtitle.size() >  0) {
				buff.append(subtitle.get(0).getPath() + ",");
				buff.append(subtitle.get(0).getLanguage() + ",");
			}
			else {
				buff.append(",,");
			}
			if(subtitle != null && subtitle.size() >  1) {
				buff.append(subtitle.get(1).getPath() + ",");
				buff.append(subtitle.get(1).getLanguage() + ",");
			}
			else {
				buff.append(",,");
			}
		}
		else {
			buff.append(",,,,,,,,,");
		}
		
		String msg = item.getResultMessage();
		if(msg != null) {
			buff.append(getResult(msg));
		}
		else {
			buff.append(",,");
		}
		
		buff.append("\n");
		return buff.toString();
	}
	
	private String getResult(String msg) {
		StringBuffer buff = new StringBuffer();
		String []lines = msg.split("\n");
		if(lines.length > 1) {
			for (String line : lines) {
				if(line.startsWith("download_complete")) {
					String []field = line.split(":|\\[|\\]");
					buff.append(field[1].trim() + ",");
					buff.append(field[2].trim() + ",");
				}
			}
		}
		else {
			buff.append(",,");
		}
		return buff.toString();
	}
}
