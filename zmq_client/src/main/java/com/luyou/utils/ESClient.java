package com.luyou.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

public class ESClient {

	private static TransportClient client;
	static {
		try {
			Settings settings = Settings.settingsBuilder()
			        .put("cluster.name", "luyou_search").build();
			client = TransportClient.builder().settings(settings).build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.1.139"), 9300))
			        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.1.139"), 9302));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public static String save(String index, String type, String json) {
		return client.prepareIndex(index, type).setSource(json).get().getId();
	}
	
//	public static String builder(String key, Object value) {
//		XContentBuilder builder = XContentFactory.jsonBuilder()
//			    .startObject()
//			        .field("user", "kimchy")
//			        .field("postDate", new Date())
//			        .field("message", "trying out Elasticsearch")
//			    .endObject();
//	}
	public static void main(String[] args) {
//		System.out.println(client.prepareGet().get());
		XContentBuilder builder = null;
		try {
			builder = XContentFactory.jsonBuilder()
				    .startObject()
				        .field("foo", "bared")
				    .endObject();
			String string = builder.string();
			System.out.println(string);
			save("test", "aaa", string);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (builder !=null) {
				builder.close();
			}
		}
	}
	
	
}
