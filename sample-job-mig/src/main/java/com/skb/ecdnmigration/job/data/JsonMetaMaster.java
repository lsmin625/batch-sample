package com.skb.ecdnmigration.job.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class JsonMetaMaster {
	private String path;
	private float duration;
	private float seg_duration;
	private String content_id;
	private String media_id;
}
