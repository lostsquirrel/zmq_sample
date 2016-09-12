package com.luyou.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.luyou.utils.MQClient;
import com.luyou.utils.RandomStringUtils;

@WebServlet("/client")
public class ClientServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ClientServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String amount = req.getParameter("amount");
		int dataAmount = Integer.parseInt(amount);
		String randomString = RandomStringUtils.randomString(dataAmount);
		log.debug(String.format("generated random string <%s>, prepare to send", randomString));
		String data = MQClient.sendMsg(randomString);
		
		resp.setContentType("text/html;charset=UTF-8");
		resp.getWriter().println(data);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	
}
