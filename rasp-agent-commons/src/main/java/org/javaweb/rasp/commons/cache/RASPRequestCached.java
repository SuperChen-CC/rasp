package org.javaweb.rasp.commons.cache;

import java.util.ArrayList;
import java.util.List;

import static org.javaweb.rasp.commons.utils.IOUtils.closeQuietly;

/**
 * RASP缓存请求对象封装,缓存请求中的各种参数信息
 * Creator: yz
 * Date: 2019-08-06
 */
public class RASPRequestCached {

	/**
	 * 输入流
	 */
	private RASPOutputStreamCache inputStreamCache;

	/**
	 * 输出流
	 */
	private RASPOutputStreamCache outputStreamCache;

	/**
	 * 输出对象，可能是Writer或者OutputStream
	 */
	private Object output;

	/**
	 * 缓存SQL语句执行记录
	 */
	private final List<Integer> sqlHashCodes = new ArrayList<Integer>();

	/**
	 * 缓存的XML，只有请求类型为application/xml时候才会缓存
	 */
	private String cachedXML;

	/**
	 * RASP Http的参数缓存
	 */
	private final RASPParameterSet<RASPCachedParameter> raspCachedParameterList =
			new RASPParameterSet<RASPCachedParameter>();

	public RASPOutputStreamCache getInputStreamCache() {
		return inputStreamCache;
	}

	public RASPOutputStreamCache getOutputStreamCache() {
		return outputStreamCache;
	}

	public void initInputStreamCache(RASPOutputStreamCache out) {
		this.inputStreamCache = out;
	}

	public void intOutputStreamCache(RASPOutputStreamCache out) {
		this.outputStreamCache = out;
	}

	public Object getOutput() {
		return output;
	}

	public void setOutput(Object output) {
		this.output = output;
	}

	/**
	 * 组装所有来自客户端的请求参数,包含:Parameter、ParameterMap、Cookies、Header、Multipart等
	 *
	 * @return 返回缓存在RASP上下文中的所有参数
	 */
	public RASPParameterSet<RASPCachedParameter> getCachedParameter() {
		return raspCachedParameterList;
	}

	/**
	 * 缓存SQL查询语句,用于避免SQL重复验证问题
	 *
	 * @param hashcode SQL hashcode
	 */
	public void cacheSqlHashCode(int hashcode) {
		this.sqlHashCodes.add(hashcode);
	}

	public boolean containsSQLQueryCache(int hashcode) {
		return sqlHashCodes.contains(hashcode);
	}

	public void cacheRequestXMLData(String cachedXML) {
		this.cachedXML = cachedXML;
	}

	public String getCachedXML() {
		return cachedXML;
	}

	/**
	 * 关闭RASP请求缓存对象，同时清除缓存数据
	 */
	public void close() {
		if (inputStreamCache != null) {
			closeQuietly(inputStreamCache);
		}

		if (outputStreamCache != null) {
			closeQuietly(outputStreamCache);
		}
	}

}
