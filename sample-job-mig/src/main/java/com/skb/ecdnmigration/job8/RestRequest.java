package com.skb.ecdnmigration.job8;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RestRequest {
	private Logger logger = LoggerFactory.getLogger(RestRequest.class);
	private static final int TIMEOUT = 3000; // 3 seconds

	RestTemplate restTemplate;
	ObjectMapper mapper = new ObjectMapper();
	
	public RestRequest() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
	    factory.setConnectTimeout(TIMEOUT);
	    factory.setReadTimeout(TIMEOUT);
	    this.restTemplate = new RestTemplate(factory);
	}
	
	public String get(String url) {
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			if(response.getStatusCode().equals(HttpStatus.OK)) {
				return response.getBody();
			}
		}
		catch(Exception e) {
			logger.info("@@@@ REST RESPONSE ERROR FROM=" + url + ", ERRROR=" + e.toString());
		}
		return error(url);
	}

	public String post(String url, String body) {
		try {
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    HttpEntity<String> request = new HttpEntity<>(body, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
			if(response.getStatusCode().equals(HttpStatus.OK)) {
				return response.getBody();
			}
		}
		catch(Exception e) {
			logger.info("@@@@ REST RESPONSE ERROR FROM=" + url + ", ERRROR=" + e.toString());
		}
		return error(url);
	}
	
	public String put(String url, String body) {
		try {
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    HttpEntity<String> request = new HttpEntity<>(body, headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
			if(response.getStatusCode().equals(HttpStatus.OK)) {
				return response.getBody();
			}
		}
		catch(Exception e) {
			logger.info("@@@@ REST RESPONSE ERROR FROM=" + url + ", ERRROR=" + e.toString());
		}
		return error(url);
	}
	
	public String delete(String url, String body) {
		try {
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    HttpEntity<String> request = new HttpEntity<>(body, headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
			if(response.getStatusCode().equals(HttpStatus.OK)) {
				return response.getBody();
			}
		}
		catch(Exception e) {
			logger.info("@@@@ REST RESPONSE ERROR FROM=" + url + ", ERRROR=" + e.toString());
		}
		return error(url);
	}
	
	private String error(String url)  {
        try {
    		JsonResult jsonResult = new JsonResult();
    		jsonResult.setResult("fail");
    		jsonResult.setError("response failed from [" + url  +"]");
    		ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(jsonResult);
		} catch (IOException e) {
			logger.error("@@@@ JSON STRING ERROR", e);
		}
		return null;
	}
}
