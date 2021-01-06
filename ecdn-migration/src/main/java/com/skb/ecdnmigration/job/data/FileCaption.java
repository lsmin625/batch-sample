package com.skb.ecdnmigration.job.data;

import lombok.Data;

@Data
public class FileCaption {
	private String mdaId;
	private String captLagFgCd;
	private String fileNn;
	private String fileByteSz;
	private String regDate;
	private String ordSeq;
	private String mdaCaptId;
	
	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append(mdaId);
		buff.append("," + captLagFgCd);
		buff.append("," + fileNn);
		buff.append("," + fileByteSz);
		buff.append("," + regDate);
		buff.append("," + ordSeq);
		buff.append("," + mdaCaptId);
		buff.append("\n");
		return buff.toString();
	}
}
