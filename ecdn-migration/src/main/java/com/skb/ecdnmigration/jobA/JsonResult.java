package com.skb.ecdnmigration.jobA;

import lombok.Data;

@Data
public class JsonResult {
	public static final String RESULT_SUCCESS = "success";
	public static final String RESULT_FAIL = "fail";
	
	String result;
	String error;
	Object body;
}
