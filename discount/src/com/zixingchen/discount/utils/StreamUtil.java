package com.zixingchen.discount.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 输入、输出流工具类
 * @author 陈梓星
 */
public class StreamUtil {
	/**
	 * 把输入流转换成字符串
	 * @param in 要转换成字符串的输入流
	 * @return 转换后的字符串，当有异常时返回空对象
	 */
	public static String convertStreamToString(InputStream in) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			return null;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
