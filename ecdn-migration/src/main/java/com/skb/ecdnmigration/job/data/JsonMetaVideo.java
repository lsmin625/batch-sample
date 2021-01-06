package com.skb.ecdnmigration.job.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class JsonMetaVideo {
	private String quality;
	private String codec;
	private String path;
	private String bitrate;
	private String filesize;
	private String resolution;
}
