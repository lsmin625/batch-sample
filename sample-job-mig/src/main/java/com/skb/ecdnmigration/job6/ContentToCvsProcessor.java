package com.skb.ecdnmigration.job6;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.skb.ecdnmigration.job.data.FileAudio;
import com.skb.ecdnmigration.job.data.FileCaption;
import com.skb.ecdnmigration.job.data.FileCommon;
import com.skb.ecdnmigration.job.data.FileList;
import com.skb.ecdnmigration.job.data.FileVideo;
import com.skb.ecdnmigration.job.data.JsonMigrationData;
import com.skb.ecdnmigration.job.data.JsonPackageInfo;
import com.skb.ecdnmigration.job.data.JsonTrackInfo;
import com.skb.ecdnmigration.job.data.TableContent;
import com.skb.ecdnmigration.job.data.VttCsv;
import com.skb.ecdnmigration.job5.VttCsvRowMapper;

public class ContentToCvsProcessor<T1, T2> implements ItemProcessor<TableContent, FileList> {
	private Logger logger = LoggerFactory.getLogger(ContentToCvsProcessor.class);
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private NamedParameterJdbcTemplate jobJdbcTemplate;
	
	public ContentToCvsProcessor(NamedParameterJdbcTemplate jobJdbcTemplate) {
		this.jobJdbcTemplate = jobJdbcTemplate;
	}
	
	private VttCsv getSize(String mediaId) {
    	StringBuffer sql = new StringBuffer();
    	sql.append("SELECT * ");
    	sql.append("FROM tb_media_size ");
       	sql.append("WHERE region='dong' ");
       	sql.append(" AND media_id='" + mediaId + "'");
       	
       	List<VttCsv> list = null;
       	try {
           	list = jobJdbcTemplate.query(sql.toString(), new VttCsvRowMapper());
           	if(list.size() > 0) {
           		return list.get(0);
           	}
       	}
       	catch(DataAccessException e) {
       		logger.error(e.toString());
       	}
       	return null;
	}

	@Override
	public FileList process(TableContent item) throws Exception {
		FileList list = new FileList();

		VttCsv vtt = getSize(item.getMediaId());

		JsonPackageInfo packInfo = item.getPackageInfo();
		JsonMigrationData migData = packInfo.getMigration_data();
		if(migData == null || NocidList.containsKey(item.getCid())) {
			logger.info("@@@@ NO MIGDATA OR FAILED-CID] MEDIA-ID=" + item.getMediaId() + ", " + packInfo.toString());
			FileCommon common = new FileCommon();
			common.setMdaId(item.getMediaId());
			list.setCommon(common);
			return list;
		}
		
		List<JsonTrackInfo> tracksArray = migData.getTrack_info();
		
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
			if(vtt != null) {
				video.setRsluFileNn(vtt.getM4vName());
				video.setRsluFileByteSz(Long.toString(vtt.getM4vSize()));
			}
			else {
				video.setRsluFileNn(track.getOutput_name() + ".m4v");
				video.setRsluFileByteSz("0");
			}
			video.setBrtKbCnt("");			//TBD
			video.setMdaRsluId("1080p");	//TBD

			video.setRsluCdecTypCd("AVC");
			video.setScrtLvlCd("3");
			video.setHdcpVerCd("HDCP_NONE");
			video.setRegDate(format.format(item.getRegisterDate()));
			video.setBitTypCd("");	//TBD
			list.getVideoList().add(video);
		}

		//to set Audio list
		trackList = getTrackList(tracksArray, "AD");
		for(JsonTrackInfo track : trackList) {
			FileAudio audio = new FileAudio();
			audio.setMdaId(item.getMediaId());
			audio.setAdoLagFgCd(track.getTrack_sub_type());
			if(vtt != null) {
				audio.setFileNm(vtt.getM4aName());
				audio.setFileByteSz(Long.toString(vtt.getM4aSize()));
			}
			else {
				audio.setFileNm(track.getOutput_name() + ".m4a");
				audio.setFileByteSz("0");
			}
			audio.setBrtKbCnt("");	//TBD

			audio.setAdoCdecTypCd("AAC");
			audio.setRegDate(format.format(item.getRegisterDate()));
			audio.setEncOrdSeq("");
			audio.setOrdSeq("");
			audio.setMdaSndId("");
			list.getAudioList().add(audio);
		}
		
		//to set Caption list
		trackList = getTrackList(tracksArray, "CP");
		for(JsonTrackInfo track : trackList) {
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
}
