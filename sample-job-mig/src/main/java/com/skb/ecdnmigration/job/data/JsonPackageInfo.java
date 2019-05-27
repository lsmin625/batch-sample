package com.skb.ecdnmigration.job.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class JsonPackageInfo {
	private String content_id;
	private String content_name;
	private String content_path;
	private String caption_path;
	private String caption_types;
	private String cdn_code;
	private String video_track_sub_type;
	private String video_track_code;
	private String media_id;
	private JsonMigrationData migration_data;
	private String data_version;
	private float duration;
}
