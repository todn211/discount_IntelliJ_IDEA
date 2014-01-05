package com.zixingchen.discount.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.loopj.android.http.RequestParams;

import android.text.TextUtils;

public abstract class TaobaoUtil {
	/**
	 * 用淘宝API访问地址
	 */
	public static final String TAOBAO_API_URL = "http://gw.api.taobao.com/router/rest";
	
	/**
	 * 淘宝商品列表URL
	 */
	public static final String TAOBAO_GOODS_ITEM_LIST_URL = "http://list.taobao.com/itemlist/default.htm";
	
	/**
	 * 淘宝单个商品URL
	 */
	public static final String TAOBAO_GOODS_ITEM_URL = "http://h5.m.taobao.com/awp/core/detail.htm";
	
	
	
	private TaobaoUtil() {
		
	}
	
	/**
	 * 构造获取单个商品信息的URL
	 * @return 单个商品信息的URL
	 */
	public static String createGoodsItemUrl(Long goodsId){
		return "http://a.m.taobao.com/i" + goodsId.toString() + ".htm";
	}

	/**
	 * 给TOP请求做HMAC签名。
	 * 
	 * @param sortedParams
	 *            所有字符型的TOP请求参数
	 * @param secret
	 *            签名密钥
	 * @return 签名
	 * @throws java.io.IOException
	 */
	public static String signTopRequestNew(
			TreeMap<String, String> sortedParams, String secret)
			throws IOException {
		// 第一步：把字典按Key的字母顺序排序,参数使用TreeMap已经完成排序

		Set<Entry<String, String>> paramSet = sortedParams.entrySet();

		// 第二步：把所有参数名和参数值串在一起
		StringBuilder query = new StringBuilder();
		for (Entry<String, String> param : paramSet) {
			if (!TextUtils.isEmpty(param.getKey())
					&& !TextUtils.isEmpty(param.getValue())) {
				query.append(param.getKey()).append(param.getValue());
			}
		}

		// 第三步：使用MD5/HMAC加密
		byte[] bytes = encryptHMAC(query.toString(), secret);

		// 第四步：把二进制转化为大写的十六进制
		return byte2hex(bytes);
	}

	private static byte[] encryptHMAC(String data, String secret)
			throws IOException {
		byte[] bytes = null;
		try {
			SecretKey secretKey = new SecretKeySpec(secret.getBytes("UTF-8"),
					"HmacMD5");
			Mac mac = Mac.getInstance(secretKey.getAlgorithm());
			mac.init(secretKey);
			bytes = mac.doFinal(data.getBytes("UTF-8"));
		} catch (GeneralSecurityException gse) {
			String msg = getStringFromException(gse);
			throw new IOException(msg);
		}
		return bytes;
	}

	private static String getStringFromException(Throwable e) {
		String result = "";
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bos);
		e.printStackTrace(ps);
		try {
			result = bos.toString("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// won't happen
		}
		return result;
	}

	private static String byte2hex(byte[] bytes) {
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex.toUpperCase());
		}
		return sign.toString();
	}

	/**
	 * 获取文件的真实后缀名。目前只支持JPG, GIF, PNG, BMP四种图片文件。
	 * 
	 * @param bytes
	 *            文件字节流
	 * @return JPG, GIF, PNG or null
	 */
	public static String getFileSuffix(byte[] bytes) {
		if (bytes == null || bytes.length < 10) {
			return null;
		}

		if (bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') {
			return "GIF";
		} else if (bytes[1] == 'P' && bytes[2] == 'N' && bytes[3] == 'G') {
			return "PNG";
		} else if (bytes[6] == 'J' && bytes[7] == 'F' && bytes[8] == 'I'
				&& bytes[9] == 'F') {
			return "JPG";
		} else if (bytes[0] == 'B' && bytes[1] == 'M') {
			return "BMP";
		} else {
			return null;
		}
	}

	/**
	 * 获取文件的真实媒体类型。目前只支持JPG, GIF, PNG, BMP四种图片文件。
	 * 
	 * @param bytes
	 *            文件字节流
	 * @return 媒体类型(MEME-TYPE)
	 */
	public static String getMimeType(byte[] bytes) {
		String suffix = getFileSuffix(bytes);
		String mimeType;

		if ("JPG".equals(suffix)) {
			mimeType = "image/jpeg";
		} else if ("GIF".equals(suffix)) {
			mimeType = "image/gif";
		} else if ("PNG".equals(suffix)) {
			mimeType = "image/png";
		} else if ("BMP".equals(suffix)) {
			mimeType = "image/bmp";
		} else {
			mimeType = "application/octet-stream";
		}

		return mimeType;
	}
   
	/**
	 * 把要发送到淘宝的参数按淘宝指定格式进行排序和加密
	 * @param parameters 要发送到淘宝的参数
	 * @param session 用户登录的session
	 * @return 按淘宝指定格式进行排序和加密的参数对象
	 * @throws java.io.IOException
	 */
   public static RequestParams createApiParams(Map<String,String> parameters, String session) throws IOException {
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("timestamp", String.valueOf(System.currentTimeMillis()));
		params.put("v", "2.0");
		params.put("app_key", "21696545");
		params.put("partner_id", "top-android-sdk");
		params.put("format", "json");
		if (!TextUtils.isEmpty(session)) {
			params.put("session", session);
		}
		params.put("sign_method", "hmac");
		params.put("method", "post");
		
		params.putAll(parameters);
		
		String sign = signTopRequestNew(params, "1d26655c651df01535676b0aaa120a8c");
		params.put("sign", sign);
		
		RequestParams requestParams = new RequestParams(params);
		return requestParams;
	}
}