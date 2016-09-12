package com.luyou.mq.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

@WebListener
public class ApplicationInitListener implements ServletContextListener {

	private static final Logger log = LoggerFactory.getLogger(ApplicationInitListener.class);

	Context context;
	int responderNum;
	// List<Thread> thList = new ArrayList<Thread>();
	Socket srouter;
	Socket sdealer;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.debug("start to init mq broker ...");
		context = ZMQ.context(10);
		srouter = context.socket(ZMQ.ROUTER);
		sdealer = context.socket(ZMQ.DEALER);
		srouter.bind("tcp://*:8888");
		sdealer.bind("tcp://*:9797");
		new Thread() {
			public void run() {
				ZMQ.proxy(srouter, sdealer, null);
			}
		}.start();
		log.debug("init mq broker finished");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		log.debug("start to destory mq broker ...");
		srouter.close();
		sdealer.close();
		context.term();
		log.debug("destory mq broker finished");
	}

}
