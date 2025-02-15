package org.javaweb.rasp.agent.hooks.cmd.handler;

import org.javaweb.rasp.commons.MethodHookEvent;
import org.javaweb.rasp.commons.attack.RASPAttackInfo;
import org.javaweb.rasp.commons.attack.RASPPosition;
import org.javaweb.rasp.commons.cache.RASPCachedParameter;
import org.javaweb.rasp.commons.cache.RASPRequestCached;
import org.javaweb.rasp.commons.context.RASPContext;
import org.javaweb.rasp.commons.utils.StringUtils;

import java.rasp.proxy.loader.HookResult;
import java.rasp.proxy.loader.RASPHookException;
import java.util.List;
import java.util.Set;

import static java.rasp.proxy.loader.HookResultType.THROW;
import static org.javaweb.rasp.agent.hooks.cmd.LocalCommandHook.CMD_TYPE;
import static org.javaweb.rasp.commons.attack.RASPAlertType.RULES;
import static org.javaweb.rasp.commons.constants.RASPConstants.DEFAULT_HOOK_RESULT;

/**
 * RASP防御本地系统命令执行示例
 * Creator: yz
 * Date: 2019-07-23
 */
public class LocalCommandHookHandler {

	private static final HookResult<?> BLOCK_RESULT = new HookResult<Object>(THROW, new RASPHookException(CMD_TYPE));

	/**
	 * 本地命令执行拦截模块，如果系统执行的CMD命令和请求参数完全一致则直接拦截
	 *
	 * @param command 执行的系统命令
	 * @param event   Hook事件
	 * @return Hook处理结果
	 */
	public static HookResult<?> processCommand(List<String> command, MethodHookEvent event) {
		String[] commands = command.toArray(new String[0]);

		// 如果当前线程中不包含HTTP请求则不需要检测
		if (event.hasRequest()) {
			RASPContext       context       = event.getRASPContext();
			RASPRequestCached cachedRequest = context.getCachedRequest();

			// 检测当前请求是否需要经过安全模块检测和过滤且该模块是否是开启状态
			if (!context.mustFilter(CMD_TYPE)) {
				return DEFAULT_HOOK_RESULT;
			}

			Set<RASPCachedParameter> cachedParameters = cachedRequest.getCachedParameter();

			// 只过滤请求参数值，忽略请求参数名称，因为参数名出现命令执行的概率太低
			for (RASPCachedParameter parameterValue : cachedParameters) {
				// 请求参数名称
				String key = parameterValue.getKey();

				// 请求参数值
				String[] values = parameterValue.getValue();

				// 请求参数出现的位置
				RASPPosition position = parameterValue.getRaspAttackPosition();

				// 遍历所有的参数值
				for (String value : values) {
					if (StringUtils.isEmpty(value)) {
						continue;
					}

					// 遍历被执行的系统命令
					for (String cmd : commands) {
						if (value.equals(cmd)) {
							// 添加攻击日志记录
							context.addAttackInfo(new RASPAttackInfo.Builder()
									.bindParameters(key, values, cmd, true)
									.bindType(position, CMD_TYPE, RULES)
									.build()
							);

							return BLOCK_RESULT;
						}
					}
				}
			}

		}

		return DEFAULT_HOOK_RESULT;
	}

}
