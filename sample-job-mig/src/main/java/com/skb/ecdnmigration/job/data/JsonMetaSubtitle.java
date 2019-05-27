package com.skb.ecdnmigration.job.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class JsonMetaSubtitle {
	private String language;
	private String path;
}
