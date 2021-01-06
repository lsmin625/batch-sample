package com.skb.ecdnmigration.job.data;

import lombok.Data;

@Data
public class FilePackageInfo {
	private String mediaId;
	private String contentId;
	private String contentName;
	private String contentPath;
	private String captionPath;
	private String captionTypes;

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append(mediaId);
		buff.append("," + contentId);
		buff.append("," + contentName);
		buff.append("," + contentPath);
		buff.append("," + captionPath);
		buff.append("," + captionTypes);
		buff.append("\n");
		return buff.toString();
	}
}
