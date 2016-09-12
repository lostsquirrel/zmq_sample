package com.luyou.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

import com.google.common.base.Strings;

public class MQClient {

	private static final Logger log = LoggerFactory.getLogger(MQClient.class);

	private static final String BUSY = "busy";

	private static final String UTF8 = "UTF-8";

	private static final String TIME_OUT = "timeout";

	private static Context context = ZMQ.context(1);
	// TODO 配置
	private static String connectionUrl;

	private static AtomicInteger requestNum = new AtomicInteger(0);
	// TODO 配置
	private static int timeout = 5 * 1000;// 连接超时时间 单位ms
	// TODO 配置
	private static int requestNumLimit = 500;// 连接最大数量限制
	

	private static MQClient instance;

	private MQClient() {
	}

	public static void init(String connectionUrl) {
		log.debug("start to init mq client ...");
		if (Strings.isNullOrEmpty(connectionUrl)) {
			throw new RuntimeException("mq.server.url.empty");
		}
		MQClient.connectionUrl = connectionUrl;
		instance = new MQClient();
		log.debug("init mq clinet finishd");
	}

	public static MQClient getInstance() {
		if (instance == null) {
			throw new RuntimeException("mq.reqeust.not.inited");
		}
		return instance;
	}

	public static String sendMsg(String msg) {
		byte[] ret = null;
		String retdata = null;
//		log.debug("requestNum retry loop start, requestNum=: " + requestNum.get());
		long sendTime = 0;
		long reciveTime = 0;
		long allTime = 0;
		int currentReqNum = 0;
		long allStart = System.currentTimeMillis();
		if (requestNum.incrementAndGet() <= requestNumLimit) {
			currentReqNum = requestNum.get();
			ZMQ.Socket requester = context.socket(ZMQ.REQ);
			requester.setSendTimeOut(timeout);
			requester.setReceiveTimeOut(timeout);
			log.debug("mq connect to : " + connectionUrl);
			requester.connect(connectionUrl);
			log.debug(String.format("current req num: %d", currentReqNum));
			 
			try {
				long sendStart = System.currentTimeMillis();
				if (requester.send(msg)) {
					sendTime = System.currentTimeMillis() - sendStart;
					log.debug(String.format("send message spend %dms", sendTime));
					long reciveStart = System.currentTimeMillis();
					ret = requester.recv();
					reciveTime = System.currentTimeMillis() - reciveStart;
					if (ret == null) {
						retdata = TIME_OUT;
					} else {
						try {
							retdata = new String(ret, UTF8);
						} catch (UnsupportedEncodingException e) {
						}

					}
				}
			} finally {
				requester.close();
				requestNum.decrementAndGet();
			}
		} else {
			retdata = BUSY;
			requestNum.decrementAndGet();
		}
		allTime = System.currentTimeMillis() - allStart;
		log.debug(String.format("process message in %dms", allTime));
//		log.debug("requestNum: " + requestNum.get());
		XContentBuilder builder = null;
		try {
			builder = XContentFactory.jsonBuilder()
				    .startObject()
				        .field("req_num", currentReqNum)
				        .field("create_time", new Date())
				        .field("send_took", sendTime)
				        .field("recv_took", reciveTime)
				        .field("all_took", allTime)
				        .field("data_size", msg.getBytes().length)
				    .endObject();
			ESClient.save("performance", "mq11", builder.string());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (builder !=null) {
				builder.close();
			}
		}
		return retdata;
	}

	public static void destory() {
		log.debug("start to destory mq client ...");
		context.term();
		log.debug("destory mq client finised");
	}
}
