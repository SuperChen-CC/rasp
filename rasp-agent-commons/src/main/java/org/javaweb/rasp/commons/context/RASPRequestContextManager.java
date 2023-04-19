package org.javaweb.rasp.commons.context;

import org.javaweb.rasp.commons.MethodHookEvent;
import org.javaweb.rasp.commons.RASPAdapterClassLoader;
import org.javaweb.rasp.commons.RASPAdapterInitialize;
import org.javaweb.rasp.commons.RASPRequestFilter;

import java.net.URL;
import java.rasp.proxy.loader.HookResult;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.rasp.proxy.loader.HookResultType.THROW;
import static org.javaweb.rasp.commons.config.RASPConfiguration.AGENT_LOGGER;
import static org.javaweb.rasp.commons.config.RASPConfiguration.MODULES_LOGGER;
import static org.javaweb.rasp.commons.constants.RASPConstants.DEFAULT_HOOK_RESULT;
import static org.javaweb.rasp.commons.utils.Base64.decodeBase64Bytes;
import static org.javaweb.rasp.loader.AgentConstants.AGENT_NAME;

public class RASPRequestContextManager {

	/**
	 * 创建org.javaweb.rasp.adapter.thread.RASPSyncThread类字节码
	 *
	 * @return 类字节码
	 */
	public static byte[] createRASPSyncThreadClass() {
		return decodeBase64Bytes(
				"yv66vgAAADIAMAoADAAeCQALAB8JAAsAIAgAIQsAIgAjBwAkCgAGACUHACYIACcHACgHACkHACoBAAhjYWxsYmFjawEA" +
						"D0xqYXZhL3V0aWwvTWFwOwEACVNpZ25hdHVyZQEANUxqYXZhL3V0aWwvTWFwPExqYXZhL2xhbmcvT2JqZWN0O0xqYXZh" +
						"L2xhbmcvT2JqZWN0Oz47AQADZW52AQASTGphdmEvbGFuZy9PYmplY3Q7AQAGPGluaXQ+AQAkKExqYXZhL3V0aWwvTWFw" +
						"O0xqYXZhL2xhbmcvT2JqZWN0OylWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEASihMamF2YS91dGlsL01hcDxMamF2" +
						"YS9sYW5nL09iamVjdDtMamF2YS9sYW5nL09iamVjdDs+O0xqYXZhL2xhbmcvT2JqZWN0OylWAQADcnVuAQADKClWAQAN" +
						"U3RhY2tNYXBUYWJsZQcAKAEAClNvdXJjZUZpbGUBABNSQVNQU3luY1RocmVhZC5qYXZhDAATABkMAA0ADgwAEQASAQAJ" +
						"aXNSdW5uaW5nBwArDAAsAC0BABFqYXZhL2xhbmcvQm9vbGVhbgwALgAvAQAQamF2YS9sYW5nL09iamVjdAEAE2RhdGFT" +
						"eW5jaHJvbml6YXRpb24BABNqYXZhL2xhbmcvRXhjZXB0aW9uAQAub3JnL2phdmF3ZWIvcmFzcC9hZGFwdGVyL3RocmVh" +
						"ZC9SQVNQU3luY1RocmVhZAEAEGphdmEvbGFuZy9UaHJlYWQBAA1qYXZhL3V0aWwvTWFwAQADZ2V0AQAmKExqYXZhL2xh" +
						"bmcvT2JqZWN0OylMamF2YS9sYW5nL09iamVjdDsBAAxib29sZWFuVmFsdWUBAAMoKVoAIQALAAwAAAACABIADQAOAAEA" +
						"DwAAAAIAEAASABEAEgAAAAIAAQATABQAAgAVAAAAMwACAAMAAAAPKrcAASortQACKiy1AAOxAAAAAQAWAAAAEgAEAAAA" +
						"DAAEAA0ACQAOAA4ADwAPAAAAAgAXAAEAGAAZAAEAFQAAAHgABQACAAAANiq0AAISBLkABQIAwAAGtgAHmQAkKrQAAgW9" +
						"AAhZAxIJU1kEKrQAA1O5AAUCAFen/9JMp//OsQABABQALgAxAAoAAgAWAAAAGgAGAAAAEgAUABQALgAWADEAFQAyABYA" +
						"NQAYABoAAAAIAAMAcAcAGwMAAQAcAAAAAgAd"
		);
	}

	/**
	 * Http请求上下文
	 */
	private static final ThreadLocal<RASPContext> RASP_CONTEXT = new InheritableThreadLocal<RASPContext>();

	public static final List<RASPRequestFilter> REQUEST_FILTER = new CopyOnWriteArrayList<RASPRequestFilter>();

	public static final List<RASPAdapterInitialize> ADAPTER_INIT = new CopyOnWriteArrayList<RASPAdapterInitialize>();

	/**
	 * 获取当前线程中的Http请求上下文
	 *
	 * @return RASP上下文
	 */
	public static RASPContext getContext() {
		return RASP_CONTEXT.get();
	}

	public static void adapterInitialize(RASPContext context, MethodHookEvent event, ClassLoader classLoader) {
		for (RASPAdapterInitialize initialize : ADAPTER_INIT) {
			initialize.init(context, event, classLoader);
		}
	}

	/**
	 * 注入Adapter类到Web应用的类加载器中，因为通常request对应的类加载器是不变的，所以该操作仅需注入一次
	 *
	 * @param obj  请求对象
	 * @param urls jar URL
	 * @return adapter loader
	 */
	public static RASPAdapterClassLoader createAdapterClassLoader(Object obj, URL... urls) {
		Class<?>    objClass       = obj.getClass();
		ClassLoader objClassLoader = objClass.getClassLoader();

		RASPAdapterClassLoader adapterClassLoader = new RASPAdapterClassLoader(urls, objClassLoader);

		// 创建RASPSyncThread类
		String threadClassName = "org.javaweb.rasp.adapter.thread.RASPSyncThread";
		adapterClassLoader.defineClass(threadClassName, createRASPSyncThreadClass());

		return adapterClassLoader;
	}

	public static RASPContext setContext(RASPContext context) {
		RASP_CONTEXT.set(context);

		if (AGENT_LOGGER.isDebugEnabled()) {
			AGENT_LOGGER.debug("{}创建RASPContext 成功！请求路径：{}", AGENT_NAME, context.getRequestPath());
		}

		return context;
	}

	/**
	 * 注册request防御模块
	 */
	public static synchronized void addHttpRequestFilter(String className) {
		try {
			Class<?> clazz = Class.forName(className);

			if (RASPRequestFilter.class.isAssignableFrom(clazz)) {
				RASPRequestFilter filter = (RASPRequestFilter) clazz.newInstance();

				if (!REQUEST_FILTER.contains(filter)) {
					REQUEST_FILTER.add(filter);
				}
			}
		} catch (Exception e) {
			AGENT_LOGGER.error(AGENT_NAME + "加载Http请求防御类：" + className + "异常：" + e, e);
		}
	}

	public static synchronized void addAdapterInitialize(String className) {
		try {
			Class<?> clazz = Class.forName(className);

			if (RASPAdapterInitialize.class.isAssignableFrom(clazz)) {
				RASPAdapterInitialize filter = (RASPAdapterInitialize) clazz.newInstance();

				if (!ADAPTER_INIT.contains(filter)) {
					ADAPTER_INIT.add(filter);
				}
			}
		} catch (Exception e) {
			AGENT_LOGGER.error(AGENT_NAME + " AdapterInitialize：" + className + "初始化异常：" + e, e);
		}
	}

	/**
	 * 检测当前线程中是否存在HTTP请求
	 *
	 * @return 是否存在HTTP请求
	 */
	public static boolean hasRequestContext() {
		RASPContext context = getContext();

		return !(context == null || context.isContextClosed());
	}

	/**
	 * 请求拦截，请求预处理检测模块
	 *
	 * @param context RASP上下文
	 * @param event   Hook事件
	 * @return Hook返回值
	 */
	public static HookResult<?> requestFilter(RASPContext context, MethodHookEvent event) {
		try {
			for (RASPRequestFilter module : REQUEST_FILTER) {
				// 调用安全模块的检测方法
				HookResult<?> result = module.filter(context, event);

				// 如果防御模块检测结果为THROW，需要终止程序执行
				if (result.getRASPHookResultType() == THROW) {
					return result;
				}
			}
		} catch (Exception e) {
			MODULES_LOGGER.error(AGENT_NAME + "Http安全过滤模块加载异常:" + e, e);
		}

		return DEFAULT_HOOK_RESULT;
	}

	/**
	 * 结束Hook点处理,如果包含恶意攻击记录：攻击日志、阻断请求。
	 * 如果Hook点是请求入口需要清空当前线程中的Http请求上下文。
	 *
	 * @param event Hook事件
	 */
	public static void finishHook(MethodHookEvent event) {
		Object thisObject = event.getThisObject();

		if (thisObject == null || !event.hasRequest()) {
			return;
		}

		RASPContext context = event.getRASPContext();

		// 检查当前请求是否是请求入口的类,如果是则清除context
		if (thisObject != context.getCacheClass()) {
			return;
		}

		try {
			// 移除RASP上下文
			if (AGENT_LOGGER.isDebugEnabled()) {
				AGENT_LOGGER.debug("{}正在清除RASPContext，请求路径：{}", AGENT_NAME, context.getRequestPath());
			}

			// 记录访问日志
			context.addAccessLog();

			// 释放context引用资源
			context.close();
		} finally {
			RASP_CONTEXT.remove();
		}
	}

}
