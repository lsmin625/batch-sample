package com.skb.ecdnmigration.job1;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.skb.ecdnmigration.job.data.FileAudio;
import com.skb.ecdnmigration.job.data.FileCaption;
import com.skb.ecdnmigration.job.data.FileList;
import com.skb.ecdnmigration.job.data.FileVideo;

public class MultiFileItemWriter implements ItemWriter<FileList> {
	private Logger logger = LoggerFactory.getLogger(MultiFileItemWriter.class);

	String common;
	String video;
	String audio;
	String caption;
	
	public MultiFileItemWriter(String common, String video, String audio, String caption) {
		this.common = common;
		this.video = video;
		this.audio = audio;
		this.caption = caption;
	}

	@Override
	public void write(List<? extends FileList> items) throws Exception {
		FileOutputStream fosCommon = new FileOutputStream(common, true);
		FileOutputStream fosVideo = new FileOutputStream(video, true);
		FileOutputStream fosAudio = new FileOutputStream(audio, true);
		FileOutputStream fosCaption = new FileOutputStream(caption, true);
		FileOutputStream fosError = new FileOutputStream(common + ".err", true);

		logger.info(">>>> WRITE BEGIN LIST-COUNT=" + items.size());
		for(FileList item : items) {
			if(item.getCommon().getCid() == null || item.getCommon().getCid().equals("")) {
				fosError.write(item.getCommon().getMdaId().getBytes());
				continue;
			}
			
			String line = item.getCommon().toString();
			fosCommon.write(line.getBytes());
			
			ArrayList<FileVideo> videoList = item.getVideoList();
			for(FileVideo file : videoList) {
				line = file.toString();
				fosVideo.write(line.getBytes());
			}

			ArrayList<FileAudio> audioList = item.getAudioList();
			for(FileAudio file : audioList) {
				line = file.toString();
				fosAudio.write(line.getBytes());
			}

			ArrayList<FileCaption> captionList = item.getCaptionList();
			for(FileCaption file : captionList) {
				line = file.toString();
				fosCaption.write(line.getBytes());
			}
		}
		logger.info("<<<< WRITE FINISHED");
		
		fosCommon.close();
		fosVideo.close();
		fosAudio.close();
		fosCaption.close();
		fosError.close();
	}
	
	

}
