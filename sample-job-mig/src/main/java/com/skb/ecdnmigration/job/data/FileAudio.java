package com.skb.ecdnmigration.job.data;

import lombok.Data;

@Data
public class FileAudio {
	private String mdaId;
	private String adoLagFgCd;
	private String fileNm;
	private String fileByteSz;
	private String adoCdecTypCd;
	private String brtKbCnt;
	private String regDate;
	private String encOrdSeq;
	private String ordSeq;
	private String mdaSndId;

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append(mdaId);
		buff.append("," + adoLagFgCd);
		buff.append("," + fileNm);
		buff.append("," + fileByteSz);
		buff.append("," + adoCdecTypCd);
		buff.append("," + brtKbCnt);
		buff.append("," + regDate);
		buff.append("," + encOrdSeq);
		buff.append("," + ordSeq);
		buff.append("," + mdaSndId);
		buff.append("\n");
		return buff.toString();
	}
}
