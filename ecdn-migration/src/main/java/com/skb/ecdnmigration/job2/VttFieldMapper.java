package com.skb.ecdnmigration.job2;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;
import com.skb.ecdnmigration.job.data.VttFile;

public class VttFieldMapper implements FieldSetMapper<VttFile> {

	@Override
	public VttFile mapFieldSet(FieldSet fieldSet) throws BindException {
		VttFile vtt = new VttFile();

		vtt.setId(fieldSet.readString("id"));
        vtt.setCol2(fieldSet.readString("col2"));
        vtt.setRw(fieldSet.readString("rw"));
        vtt.setCol4(fieldSet.readString("col4"));
        vtt.setOwner(fieldSet.readString("owner"));
        vtt.setGroup(fieldSet.readString("group"));
        vtt.setSize(fieldSet.readString("size"));
        vtt.setMonth(fieldSet.readString("month"));
        vtt.setDay(fieldSet.readString("day"));
        vtt.setYear(fieldSet.readString("year"));
        vtt.setVtt(fieldSet.readString("vtt"));
        
		return vtt;
	}

}
