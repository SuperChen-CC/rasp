package org.javaweb.rasp.commons.context;

import org.javaweb.rasp.commons.MethodHookEvent;
import org.javaweb.rasp.commons.RASPCallback;

import java.rasp.proxy.loader.HookResult;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.rasp.proxy.loader.HookResultType.RETURN;
import static org.javaweb.rasp.commons.constants.RASPConstants.DEFAULT_STRING_RESULT;
import static org.javaweb.rasp.commons.context.RASPRequestContextManager.*;
import static org.javaweb.rasp.commons.log.RASPLogger.errorLog;

public class RASPContextBuilder {

	public static AtomicBoolean adapterInjected = new AtomicBoolean(false);

	private static final Map<String, AtomicBoolean> APP_INIT_MAP = new ConcurrentHashMap<String, AtomicBoolean>();

	/**
	 * 创建RASP上下文
	 *
	 * @param e    方法调用事件
	 * @param call 回调方法
	 * @return HookResult
	 */
	public static HookResult<?> createContext(MethodHookEvent e, RASPCallback<RASPContext> call) {
		return createContext(e, null, call);
	}

	/**
	 * 创建RASP上下文
	 *
	 * @param e      方法调用事件
	 * @param loader Hook到的应用类加载器
	 * @param call   回调方法
	 * @return HookResult
	 */
	public static HookResult<?> createContext(MethodHookEvent e, ClassLoader loader, RASPCallback<RASPContext> call) {
		if (!e.hasRequest()) {
			RASPContext context;

			if (adapterInjected.compareAndSet(false, true)) {
				context = createAdapter(e, loader, call);
			} else {
				context = call.callback(e);
			}

			if (context != null) {
				setContext(context);

				String contextName = context.getContextName();

				// App第一次访问时需要初始化
				if (!APP_INIT_MAP.containsKey(contextName)) {
					APP_INIT_MAP.put(contextName, new AtomicBoolean(false));

					if (APP_INIT_MAP.get(contextName).compareAndSet(false, true)) {
						// 初始化App
						appInitialize(context, e, loader);
					}
				}

				// 加载Http安全校验模块
				HookResult<?> result = requestFilter(context, e);

				if (result.getRASPHookResultType() != RETURN) {
					return result;
				}
			}
		}

		return DEFAULT_STRING_RESULT;
	}

	private static RASPContext createAdapter(MethodHookEvent e, ClassLoader loader, RASPCallback<RASPContext> call) {
		try {
			Object thisObject = e.getThisObject();

			if (loader == null) {
				// 静态方法没有this对象，传入的是该类的类名
				if (thisObject == null || thisObject instanceof String) {
					return null;
				}

				loader = thisObject.getClass().getClassLoader();
			}

			// 创建RASP上下文
			RASPContext context = call.callback(e);

			// 类注入完成后调用adapter的初始化方法
			adapterInitialize(context, e, createAdapterClassLoader(loader));

			return context;
		} catch (Exception ex) {
			errorLog("创建Context适配器异常:", ex);

			return null;
		}
	}

}
