package com.luyou.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import com.google.common.base.Strings;

public class RandomStringUtils {

	private static String source = "";

	private static int sourceLength;

	private static Random r = new Random();

//	private static final Logger log = LoggerFactory.getLogger(RandomStringUtils.class);

	public static String randomString() {
		return randomString(Math.abs(r.nextInt(sourceLength)));
	}

	public static String randomString(int length) {
		StringBuilder sb = new StringBuilder();
		if (length > sourceLength) {
			int x = length / sourceLength;
			length = length % sourceLength;
			while (x > 0) {
				x--;
				sb.append(source);
			}
		}
		int nextInt = Math.abs(r.nextInt(sourceLength - length));
		sb.append(source.substring(nextInt, nextInt + length));
		return sb.toString();
	}

	static {
		InputStream in = RandomStringUtils.class.getClassLoader().getResourceAsStream("chinese_names.txt");

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		String tmp = null;
		do {
			try {
				tmp = br.readLine();
			} catch (IOException e) {
			} finally {

			}
			if (!Strings.isNullOrEmpty(tmp)) {
				tmp.trim();
				sb.append(tmp);
			}
		} while (tmp != null);
		source = sb.toString();
		sourceLength = source.length();

		try {
			br.close();
		} catch (IOException e) {
		}
		try {
			in.close();
		} catch (IOException e) {
		}
		
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			System.out.println(RandomStringUtils.randomString(22));
			// com.luyou.utils.RandomStringUtils
		}
	}
}
