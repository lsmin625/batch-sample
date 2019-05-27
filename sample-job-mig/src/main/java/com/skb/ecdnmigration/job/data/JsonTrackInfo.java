package com.skb.ecdnmigration.job.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class JsonTrackInfo {
	private String input_file_path;
	private String output_name;
	private int track_id;
	private String track_type;
	private String track_code;
	private String track_sub_type;
	private String track_name;
	private String clean_path;
	private String clean_name;
}
