package com.skb.ecdnmigration.job.data;

import lombok.Data;

@Data
public class FileCheck {
	private String mediaId;
	private String contentId;
	private String contentName;
	private String contentPath;
	private String contentFile;
	private String cleanName;

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append(mediaId);
		buff.append("," + contentName);
		buff.append("," + contentPath);
		buff.append("," + contentFile);
		buff.append("," + cleanName);
		buff.append("\n");
		return buff.toString();
	}
}
