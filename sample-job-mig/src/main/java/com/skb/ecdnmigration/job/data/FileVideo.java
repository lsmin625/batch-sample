package com.skb.ecdnmigration.job.data;

import lombok.Data;

@Data
public class FileVideo {
	private String mdaId;
	private String rsluTypCd;
	private String rsluFileNn;
	private String rsluFileByteSz;
	private String brtKbCnt;
	private String rsluCdecTypCd;
	private String scrtLvlCd;
	private String hdcpVerCd;
	private String regDate;
	private String bitTypCd;
	private String mdaRsluId;

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append(mdaId);
		buff.append("," + rsluTypCd);
		buff.append("," + rsluFileNn);
		buff.append("," + rsluFileByteSz);
		buff.append("," + brtKbCnt);
		buff.append("," + rsluCdecTypCd);
		buff.append("," + scrtLvlCd);
		buff.append("," + hdcpVerCd);
		buff.append("," + regDate);
		buff.append("," + bitTypCd);
		buff.append("," + mdaRsluId);
		buff.append("\n");
		return buff.toString();
	}
}
