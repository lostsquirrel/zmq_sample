package com.luyou.mq.worker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class ApplicationInitListener implements ServletContextListener {

	private ThreadPoolExecutor tp;

	private BlockingQueue<Runnable> workQueue;
	
	private static final int WORKER_LIMIT_NUM = 10;
	
	private static final Logger log = LoggerFactory.getLogger(ApplicationInitListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.debug("start to init mq worker ...");
		workQueue = new ArrayBlockingQueue<Runnable>(WORKER_LIMIT_NUM);
		tp = new ThreadPoolExecutor(1, 10, 1, TimeUnit.MINUTES, workQueue);
		for (int i = 0; i < WORKER_LIMIT_NUM; i++) {
			tp.execute(new MQWorker());
		}
		log.debug("init mq worker finished");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		log.debug("start to destory mq worker ...");
		MQWorker.destory();
		tp.shutdown();
		workQueue.clear();
		log.debug("destory mq worker finished");
	}

}
