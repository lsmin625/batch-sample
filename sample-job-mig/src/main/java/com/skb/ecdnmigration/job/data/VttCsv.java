package com.skb.ecdnmigration.job.data;

import lombok.Data;

@Data
public class VttCsv {
	String region;
	String mediaId;
	String cid;
	String contentName;
	String m4aName;
	long m4aSize;
	String m4vName;
	long m4vSize;
	String mp4Name;
	long mp4Size;
	long ratio;
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append(region + ",");
		buff.append(mediaId + ",");
		buff.append(cid + ",");
		buff.append(contentName + ",");
		buff.append(m4aName + ",");
		buff.append(m4aSize + ",");
		buff.append(m4vName + ",");
		buff.append(m4vSize + ",");
		buff.append(mp4Name + ",");
		buff.append(mp4Size + ",");
		if(mp4Size != 0L) {
			ratio = (m4aSize + m4vSize - mp4Size) * 100L / mp4Size;
			buff.append(ratio);
		}
		buff.append("\n");
		return buff.toString();
	}
}
