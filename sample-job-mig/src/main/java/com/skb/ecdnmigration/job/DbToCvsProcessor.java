package com.skb.ecdnmigration.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.springframework.batch.item.ItemProcessor;

import com.skb.ecdnmigration.job.data.FileAudio;
import com.skb.ecdnmigration.job.data.FileCaption;
import com.skb.ecdnmigration.job.data.FileCommon;
import com.skb.ecdnmigration.job.data.FileList;
import com.skb.ecdnmigration.job.data.FileVideo;
import com.skb.ecdnmigration.job.data.JsonMetaAudio;
import com.skb.ecdnmigration.job.data.JsonMetaInfo;
import com.skb.ecdnmigration.job.data.JsonMetaVideo;
import com.skb.ecdnmigration.job.data.JsonMigrationData;
import com.skb.ecdnmigration.job.data.JsonPackageInfo;
import com.skb.ecdnmigration.job.data.JsonTrackInfo;
import com.skb.ecdnmigration.job.data.TableContent;

public class DbToCvsProcessor<T1, T2> implements ItemProcessor<TableContent, FileList> {
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public FileList process(TableContent item) throws Exception {
		FileList list = new FileList();

		JsonPackageInfo packInfo = item.getPackageInfo();
		JsonMigrationData migData = packInfo.getMigration_data();
		List<JsonTrackInfo> tracksArray = migData.getTrack_info();
		JsonMetaInfo metaInfo = item.getMetaInfo();
		JsonMetaVideo metaVideo = metaInfo.getVideo().get(0);
		JsonMetaAudio metaAudio = metaInfo.getAudio().get(0);
//		List<JsonMetaSubtitle> metaSubtitleList = metaInfo.getSubtitle();
		
		//to set Common
		FileCommon common = new FileCommon();
		common.setMdaId(item.getMediaId());
		common.setFileNm(migData.getMaster_m3u8_name() + ".m3u8");
		common.setFilePath(migData.getDirectory_path());
		common.setAllPlayTmsc(Float.toString(packInfo.getDuration()));
		common.setDrmTypCd("WIDEVINE");
		common.setDrmMthdCd("CTR");
		common.setSendMthdCd("HLS");
		common.setPreexamFrTmsc("0");
		common.setPreexamToTmsc("0");
		common.setCleanFileNm("");	//setting value at video track
		common.setRegDate(format.format(item.getRegisterDate()));
		common.setCid(item.getCid());
		common.setEpsdId("");
		common.setSoundTypCd("");
		common.setRightTypCd("");
		common.setMdaStatCd("");
		list.setCommon(common);


		//to set Video list
		List<JsonTrackInfo> trackList = getTrackList(tracksArray, "VD");
		for(JsonTrackInfo track : trackList) {
			common.setCleanFileNm(track.getClean_name());
			
			FileVideo video = new FileVideo();
			video.setMdaId(item.getMediaId());
			video.setRsluTypCd(track.getTrack_sub_type());
			video.setRsluFileNn(getFileName(metaVideo.getPath()));
			video.setRsluFileByteSz(metaVideo.getFilesize());	//with meta
			video.setBrtKbCnt(metaVideo.getBitrate());	//with meta
			video.setRsluCdecTypCd("AVC");
			video.setScrtLvlCd("3");
			video.setHdcpVerCd("HDCP_NONE");
			video.setRegDate(format.format(item.getRegisterDate()));
			video.setBitTypCd("");	//TBD
			video.setMdaRsluId(metaVideo.getResolution()); //not sure
			list.getVideoList().add(video);
		}

		//to set Audio list
		trackList = getTrackList(tracksArray, "AD");
		for(JsonTrackInfo track : trackList) {
			FileAudio audio = new FileAudio();
			audio.setMdaId(item.getMediaId());
			audio.setAdoLagFgCd(track.getTrack_sub_type());
			audio.setFileNm(getFileName(metaAudio.getPath()));
			audio.setFileByteSz(metaAudio.getFilesize());	//with meta
			audio.setAdoCdecTypCd("AAC");
			audio.setBrtKbCnt(metaAudio.getBitrate());	//with meta
			audio.setRegDate(format.format(item.getRegisterDate()));
			audio.setEncOrdSeq("");
			audio.setOrdSeq("");
			audio.setMdaSndId("");
			list.getAudioList().add(audio);
		}
		
		//to set Caption list
		trackList = getTrackList(tracksArray, "CP");
		for(JsonTrackInfo track : trackList) {
//			JsonMetaSubtitle metaSubtitle = getMetaSubtitle(metaSubtitleList, track.getTrack_name());
			
			FileCaption caption = new FileCaption();
			caption.setMdaId(migData.getMedia_id());
			caption.setCaptLagFgCd(track.getTrack_sub_type());
			caption.setFileNn(track.getOutput_name() + ".vtt");
			caption.setFileByteSz("0");	//TBD
			caption.setRegDate(format.format(item.getRegisterDate()));
			caption.setOrdSeq("0");
			caption.setMdaCaptId("");
			list.getCaptionList().add(caption);
		}
		
		return list;
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
	
	private static String getFileName(String path) {
		int index = path.lastIndexOf('/');
		return path.substring(index + 1);
	}

	/*
	private static JsonMetaSubtitle getMetaSubtitle(List<JsonMetaSubtitle> tracksArray, String type) {
		for(JsonMetaSubtitle track : tracksArray) {
			if(type.equalsIgnoreCase(track.getLanguage())) {
				return track;
			}
		}
		return null;
	}
	*/
}
