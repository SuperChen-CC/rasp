package org.javaweb.rasp.commons.context;

import org.javaweb.rasp.commons.MethodHookEvent;
import org.javaweb.rasp.commons.RASPCallback;

import java.rasp.proxy.loader.HookResult;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.rasp.proxy.loader.HookResultType.RETURN;
import static org.javaweb.rasp.commons.config.RASPConfiguration.AGENT_LOGGER;
import static org.javaweb.rasp.commons.constants.RASPConstants.DEFAULT_STRING_RESULT;
import static org.javaweb.rasp.commons.context.RASPRequestContextManager.*;
import static org.javaweb.rasp.loader.AgentConstants.AGENT_NAME;

public class RASPContextBuilder {

	public static HookResult<?> createContext(MethodHookEvent e, AtomicBoolean lock, RASPCallback<RASPContext> call) {
		if (!e.hasRequest()) {
			RASPContext context;

			if (lock.compareAndSet(false, true)) {
				// 创建RASP上下文，如果是静态文件请求不需要创建上下文，所以有可能会返回null
				context = createAdapter(e, call);
			} else {
				context = call.callback(e);
			}

			if (context != null) {
				// 加载Http安全校验模块
				HookResult<?> result = requestFilter(context, e);

				if (result.getRASPHookResultType() != RETURN) {
					return result;
				}
			}
		}

		return DEFAULT_STRING_RESULT;
	}

	private static RASPContext createAdapter(MethodHookEvent event, RASPCallback<RASPContext> callback) {
		try {
			Object      thisObject = event.getThisObject();
			ClassLoader loader;

			// 静态方法没有this对象，传入的是该类的Class对象
			if (thisObject instanceof Class) {
				loader = ((Class<?>) thisObject).getClassLoader();
			} else {
				loader = thisObject.getClass().getClassLoader();
			}

			// 创建RASP上下文
			RASPContext context = callback.callback(event);

			// 类注入完成后调用adapter的初始化方法
			adapterInitialize(context, event, createAdapterClassLoader(loader));

			return context;
		} catch (Exception ex) {
			AGENT_LOGGER.error(AGENT_NAME + "创建Servlet适配器异常:" + ex, ex);

			return null;
		}
	}

}
