package com.skb.ecdnmigration.job.data;

import java.util.ArrayList;

import lombok.Data;

@Data
public class FileList {
	private FileCommon common;
	private ArrayList<FileVideo> videoList = new ArrayList<FileVideo>();
	private ArrayList<FileAudio> audioList = new ArrayList<FileAudio>();
	private ArrayList<FileCaption> captionList = new ArrayList<FileCaption>();
}
