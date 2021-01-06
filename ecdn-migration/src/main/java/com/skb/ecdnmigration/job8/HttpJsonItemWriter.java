package com.skb.ecdnmigration.job8;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class HttpJsonItemWriter implements ItemWriter<String> {
	private RestRequest restRequester = new RestRequest();
	String url;
	
	public HttpJsonItemWriter(String url) {
		this.url = url;
	}

	@Override
	public void write(List<? extends String> items) throws Exception {
		for(String item : items) {
			restRequester.post(url, item);
		}
	}
}
