package com.luyou.mq.worker;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class MQWorker implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(MQWorker.class);
	
	private static final String DISPOSE = "0000000000000000000000000";
	
	private static final String EXCEPTION = "exception";

	private static int workerNum = 0;
//		TODO 配置
	static String addr = "tcp://192.168.1.139:9797";
	
	@Override
	public void run() {
		workerNum++;
		Context context = ZMQ.context(1);
		Socket responder = context.socket(ZMQ.REP);
		responder.connect(addr);
        log.debug("worker connect to " + addr);
        while(!Thread.currentThread().isInterrupted()) {
            //接收发送端的数据
			String requestStr = responder.recvStr(Charset.forName("UTF-8"));
			
            log.debug(String.format("recived message <%s>", requestStr));
            if(DISPOSE.equals(requestStr)){
            	responder.send(DISPOSE, ZMQ.NOBLOCK);
            	break;
            }
            String sendData = null;
            try{
                sendData = requestStr;
            }catch(Exception e){//即使出异常也能够维持住监听
            	sendData = EXCEPTION;
            }finally{
            	//回应发起端的请求
                try {
                	responder.send(sendData.getBytes("utf-8"), 0);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
            }
        }
        
        responder.close();
        context.close();
        context.term();
        log.debug("worker destoryed");
	}
	
	public static void destory() {
		Context context = ZMQ.context(1);
		for (int i = 0; i < workerNum; i++) {
			Socket req = context.socket(ZMQ.REQ);
			req.connect(addr);
			req.send(DISPOSE, ZMQ.NOBLOCK);
			req.recv(ZMQ.NOBLOCK);
			req.close();
		}
		context.term();
	}

}
