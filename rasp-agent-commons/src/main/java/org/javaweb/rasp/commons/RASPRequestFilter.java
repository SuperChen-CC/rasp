package org.javaweb.rasp.commons;

import org.javaweb.rasp.commons.context.RASPContext;

import java.rasp.proxy.loader.HookResult;

/**
 * Http请求安全验证
 * Created by yz on 2017/1/18.
 *
 * @author yz
 */
public interface RASPRequestFilter {

	/**
	 * 参数过滤
	 *
	 * @param context RASP上下文
	 * @param event   Hook事件
	 * @return 过滤结果
	 */
	HookResult<?> filter(RASPContext context, MethodHookEvent event);

}
