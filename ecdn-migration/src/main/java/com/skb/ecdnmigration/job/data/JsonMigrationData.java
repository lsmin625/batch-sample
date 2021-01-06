package com.skb.ecdnmigration.job.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class JsonMigrationData {
	private String media_id;
	private String directory_path;
	private String master_m3u8_name;
	private float segment_duration;
	private float fragment_duration;
	private float clear_duration;
	private List<JsonTrackInfo> track_info;
}
