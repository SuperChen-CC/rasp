package org.javaweb.rasp.commons.cache;

import org.javaweb.rasp.commons.RASPParameterFilter;
import org.javaweb.rasp.commons.context.RASPContext;

import java.rasp.proxy.loader.HookResult;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.rasp.proxy.loader.HookResultType.RETURN;
import static org.javaweb.rasp.commons.constants.RASPConstants.DEFAULT_STRING_ARRAY_RESULT;
import static org.javaweb.rasp.commons.log.RASPLogger.errorLog;

public class RASPParameterSet<E> extends HashSet<E> {

	private static final List<RASPParameterFilter> PARAMETER_FILTER = new CopyOnWriteArrayList<RASPParameterFilter>();

	public static void addParameterFilter(String className) {
		try {
			Class<?> clazz = Class.forName(className);

			if (RASPParameterFilter.class.isAssignableFrom(clazz)) {
				RASPParameterFilter filter = (RASPParameterFilter) clazz.newInstance();

				if (!PARAMETER_FILTER.contains(filter)) {
					PARAMETER_FILTER.add(filter);
				}
			}
		} catch (Exception e) {
			errorLog("加载Http请求防御类：" + className + "异常：", e);
		}
	}

	public HookResult<String[]> cacheParameter(RASPCachedParameter parameter, RASPContext context) {
		if (contains(parameter)) {
			return DEFAULT_STRING_ARRAY_RESULT;
		}

		// 缓存请求参数
		add((E) parameter);

		// 检测参数合法性
		for (RASPParameterFilter filter : PARAMETER_FILTER) {
			HookResult<String[]> result = filter.filter(parameter, context);

			// 如果防御模块检测结果为THROW/REPLACE_OR_BLOCK，需要终止程序执行
			if (result.getRASPHookResultType() != RETURN) {
				return result;
			}
		}

		return DEFAULT_STRING_ARRAY_RESULT;
	}

}
