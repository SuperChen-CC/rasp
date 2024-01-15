package org.javaweb.rasp.commons.utils;

import org.javaweb.rasp.commons.attack.RASPPosition;
import org.javaweb.rasp.commons.cache.RASPCachedParameter;
import org.javaweb.rasp.commons.cache.RASPRequestCached;
import org.javaweb.rasp.commons.context.RASPServletRequestContext;
import org.javaweb.rasp.commons.lru.ConcurrentLinkedHashMap;
import org.javaweb.rasp.commons.servlet.HttpServletRequestProxy;
import org.javaweb.rasp.commons.servlet.HttpServletResponseProxy;
import org.javaweb.rasp.commons.servlet.HttpSessionProxy;
import org.javaweb.rasp.commons.servlet.ServletContextProxy;

import java.io.File;
import java.net.URL;
import java.util.*;

import static org.javaweb.rasp.commons.attack.RASPPosition.*;
import static org.javaweb.rasp.commons.config.RASPConfiguration.AGENT_PROPERTIES;
import static org.javaweb.rasp.commons.constants.RASPConstants.DEFAULT_CACHE_COUNT;
import static org.javaweb.rasp.commons.log.RASPLogger.errorLog;
import static org.javaweb.rasp.commons.utils.IPV4Utils.textToNumericFormatV4;
import static org.javaweb.rasp.commons.utils.IPV4Utils.textToNumericFormatV6;
import static org.javaweb.rasp.commons.utils.StringUtils.checkMaxLength;
import static org.javaweb.rasp.commons.utils.StringUtils.isNotEmpty;
import static org.javaweb.rasp.loader.AgentConstants.AGENT_NAME;

/**
 * Created by yz on 2017/1/17.
 *
 * @author yz
 */
public class HttpServletRequestUtils extends RASPRequestUtils {

	private static final String TMP_DIR = System.getProperty("java.io.tmpdir");

	private static final int MAX_LENGTH = 5000;

	/**
	 * 缓存Web应用ContextPath
	 */
	private static final Map<String, File> DOCUMENT_ROOT_MAP = new ConcurrentLinkedHashMap
			.Builder<String, File>().maximumWeightedCapacity(DEFAULT_CACHE_COUNT).build();

	public static ServletContextProxy getServletContext(HttpServletRequestProxy request) {
		ServletContextProxy sc = request.getServletContext();

		if (sc == null) {
			// 适配Servlet3.1以下版本
			HttpSessionProxy session = request.getSession(false);

			if (session == null) {
				// 直接getSession(true)会影响到shiro创建session
				session = request.getSession(true);
			}

			return session != null ? session.getServletContext() : null;
		}

		return sc;
	}

	/**
	 * 获取web目录,Weblogic 默认以war包部署的时候不能用getRealPath,xxx.getResource("/")获取
	 * 的是当前应用所在的类路径，截取到WEB-INF之后的路径就是当前应用的web根目录了
	 *
	 * @param context RASP上下文
	 * @return Web根目录
	 */
	public static File getDocumentRootFile(RASPServletRequestContext context) {
		String                  contextPath  = context.getContextPath();
		HttpServletRequestProxy request      = context.getServletRequest();
		File                    documentRoot = DOCUMENT_ROOT_MAP.get(contextPath);

		if (documentRoot != null) {
			return documentRoot;
		}

		String webRoot = request.getRealPath("/");

		// 排除SpringBoot默认使用的/tmp/目录
		if (webRoot != null && !webRoot.isEmpty() && isTempDir(webRoot)) {
			return new File(webRoot);
		}

		ServletContextProxy sc = getServletContext(request);

		if (sc != null) {
			try {
				// 处理Servlet 3+，无法获取getRealPath的情况，如：Weblogic
				URL resource = sc.getResource("/");

				if (resource != null && isTempDir(resource.getFile())) return new File(resource.getFile());
			} catch (Throwable t) {
				try {
					// Servlet 2.x，request.getSession()可能会导致shiro的session无法获取，
					URL resource = sc.getResource("/");

					if (resource != null && isTempDir(resource.getFile())) return new File(resource.getFile());
				} catch (Throwable ignored) {
				}
			}
		}

		documentRoot = getWebRoot(request.getClass().getClassLoader());

		// 缓存Web应用路径
		DOCUMENT_ROOT_MAP.put(contextPath, documentRoot);

		return documentRoot;
	}

	public static boolean isTempDir(String dir) {
		return !dir.startsWith(TMP_DIR);
	}

	public static File getWebRoot(ClassLoader loader) {
		String webRoot = null;

		try {
			URL resource = loader.getResource("/");

			// getResource("/")可能会获取不到Resource
			if (resource == null) {
				resource = loader.getResource("");
			}

			if (resource != null) {
				if ("jar".equals(resource.getProtocol())) {
					webRoot = resource.getPath();

					webRoot = webRoot.substring(webRoot.indexOf(":") + 1, webRoot.lastIndexOf("!/"));
				} else {
					webRoot = resource.getPath();
				}
			}

			if (webRoot != null) {
				return new File(webRoot).getParentFile();
			}
		} catch (Exception ignored) {
		}

		// 如果上面的方法仍无法获取Web目录，以防万一返回一个当前文件路径
		return new File("").getAbsoluteFile();
	}

	/**
	 * 如果经过nginx反向代理后可能会获取到一个本地的IP地址如:127.0.0.1、192.168.1.100，
	 * 配置nginx把客户端真实IP地址放到nginx请求头中的x-real-ip或x-forwarded-for的值。
	 * 优先使用用户配置的请求头IP字段，然后再通过HTTP请求连接获取IP。
	 *
	 * @param request 请求对象
	 * @return 获取客户端IP
	 */
	public static String getRemoteAddr(HttpServletRequestProxy request) {
		String ipKey = AGENT_PROPERTIES.getProxyIpHeader();

		if (isNotEmpty(ipKey)) {
			String proxyIP = request.getHeader(ipKey);

			if (proxyIP != null) {
				if (textToNumericFormatV4(proxyIP) != null) return proxyIP;
				if (textToNumericFormatV6(proxyIP) != null) return proxyIP;
			}
		}

		String ip = request.getRemoteAddr();

		return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
	}

	/**
	 * 获取Http请求头对象
	 *
	 * @param request 请求对象
	 * @return 请求头Map
	 */
	public static Map<String, String> getRequestHeaderMap(HttpServletRequestProxy request) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		Enumeration<String> e   = request.getHeaderNames();

		while (e.hasMoreElements()) {
			String name = e.nextElement();
			map.put(name, request.getHeader(name));
		}

		return map;
	}

	/**
	 * 获取Http请求头对象
	 *
	 * @param response 响应对象
	 * @return 响应头Map
	 */
	public static Map<String, String> getResponseHeaderMap(HttpServletResponseProxy response) {
		Map<String, String> map     = new LinkedHashMap<String, String>();
		Collection<String>  headers = response.getHeaderNames();

		if (headers != null) {
			map.put("Status", String.valueOf(response.getStatus()));

			for (String name : headers) {
				if ("Status".equalsIgnoreCase(name)) {
					continue;
				}

				map.put(name, response.getHeader(name));
			}
		}

		return map;
	}

	/**
	 * 获取Http请求中的参数，用于日志记录
	 *
	 * @param context RASP上下文
	 * @return 参数Map
	 */
	public static Map<String, String[]> getLogParameterMap(RASPServletRequestContext context) {
		Map<String, String[]> logMap = null;

		try {
			HttpServletRequestProxy servletRequest = context.getServletRequest();
			RASPRequestCached       cachedRequest  = context.getCachedRequest();
			Map<String, String[]>   map            = servletRequest.getParameterMap();
			logMap = new HashMap<String, String[]>();

			if (map != null && !map.isEmpty()) {
				for (String key : map.keySet()) {
					key = checkMaxLength(key, MAX_LENGTH);
					String[] values = checkMaxLength(map.get(key), MAX_LENGTH);

					logMap.put(key, values);
				}
			}

			// 缓存的被调用过的参数集合
			Set<RASPCachedParameter> cachedParameterList = cachedRequest.getCachedParameter();

			// 缓存ParameterMap
			for (RASPCachedParameter parameter : cachedParameterList) {
				RASPPosition position = parameter.getRaspAttackPosition();

				// 不记录ParameterMap、JSON、XML，JSON/XML请求的body单独记录
				if (PARAMETER_MAP == position || position == JSON || position == XML) {
					continue;
				}

				String   key    = checkMaxLength(parameter.getKey(), MAX_LENGTH);
				String[] values = checkMaxLength(parameter.getValue(), MAX_LENGTH);

				if (map != null) {
					String[] val = map.get(key);

					if (Arrays.equals(val, values)) {
						continue;
					}
				}

				logMap.put(key, values);
			}
		} catch (Exception e) {
			errorLog("{}记录日志异常：{}", AGENT_NAME, e.toString());
		}

		return logMap;
	}

}
