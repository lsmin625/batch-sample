package com.sk.batch.samplejobbean.job01.step1;

import java.util.Date;

import org.springframework.batch.item.ItemProcessor;

import com.sk.batch.samplejobbean.job01.data.User;
import com.sk.batch.samplejobbean.job01.data.UserXml;

public class CsvToXmlProcessor implements ItemProcessor<User, UserXml> {

	@Override
	public UserXml process(User item) throws Exception {
        UserXml userXml = new UserXml();
        userXml.setUserName(item.getUserName());
        userXml.setUserId(item.getUserId());
        userXml.setTransactionDate(item.getTransactionDate());
        userXml.setTransactionAmount(item.getTransactionAmount());
        userXml.setUpdatedDate(new Date());
        return userXml;
	}

}
