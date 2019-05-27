package com.skb.ecdnmigration.job.data;

import java.util.Date;
import lombok.Data;

@Data
public class TableContent {
	private int contentSeq;
	private String mediaId;
	private String cid;
	private JsonPackageInfo packageInfo;
	private JsonMetaInfo metaInfo;
	private Date registerDate;
}
