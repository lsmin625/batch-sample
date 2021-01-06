package com.skb.ecdnmigration.job.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class JsonMetaInfo {
	private JsonMetaMaster master;
	private List<JsonMetaVideo> video;
	private List<JsonMetaAudio> audio;
	private List<JsonMetaSubtitle> subtitle;
}
