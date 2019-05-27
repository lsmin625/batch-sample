package com.skb.ecdnmigration.job.data;

import lombok.Data;

@Data
public class FileCommon {
	private String mdaId;
	private String fileNm;
	private String filePath;
	private String allPlayTmsc;
	private String drmTypCd;
	private String drmMthdCd;
	private String sendMthdCd;
	private String preexamFrTmsc;
	private String preexamToTmsc;
	private String cleanFileNm;
	private String regDate;
	private String cid;
	private String epsdId;
	private String soundTypCd;
	private String rightTypCd;
	private String mdaStatCd;	

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append(mdaId);
		buff.append("," + fileNm);
		buff.append("," + filePath);
		buff.append("," + allPlayTmsc);
		buff.append("," + drmTypCd);
		buff.append("," + drmMthdCd);
		buff.append("," + sendMthdCd);
		buff.append("," + preexamFrTmsc);
		buff.append("," + preexamToTmsc);
		buff.append("," + cleanFileNm);
		buff.append("," + regDate);
		buff.append("," + cid);
		buff.append("," + epsdId);
		buff.append("," + soundTypCd);
		buff.append("," + rightTypCd);
		buff.append("," + mdaStatCd);
		buff.append("\n");
		return buff.toString();
	}
}
