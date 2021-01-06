package com.skb.ecdnmigration.job2;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.skb.ecdnmigration.job.data.FileCaption;
import com.skb.ecdnmigration.job.data.VttFile;

public class CaptionFieldMapper implements FieldSetMapper<FileCaption> {

	@Override
	public FileCaption mapFieldSet(FieldSet fieldSet) throws BindException {
		FileCaption vtt = new FileCaption();

		vtt.setMdaId(fieldSet.readString("id"));
		vtt.setCaptLagFgCd(fieldSet.readString("cd"));
		vtt.setFileNn(fieldSet.readString("name"));
		vtt.setFileByteSz(fieldSet.readString("size"));
		vtt.setRegDate(fieldSet.readString("date"));
		vtt.setOrdSeq(fieldSet.readString("seq"));
		vtt.setMdaCaptId(fieldSet.readString("cid"));

		return vtt;
	}

}
