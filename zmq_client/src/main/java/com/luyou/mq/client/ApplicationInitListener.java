package com.luyou.mq.client;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.luyou.utils.MQClient;

@WebListener
public class ApplicationInitListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String connectionUrl = "tcp://192.168.1.139:8888";
		MQClient.init(connectionUrl);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		MQClient.destory();
	}

}
