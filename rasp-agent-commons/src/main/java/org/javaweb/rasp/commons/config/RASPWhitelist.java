package org.javaweb.rasp.commons.config;

import org.javaweb.rasp.commons.context.RASPContext;

import static org.javaweb.rasp.commons.log.RASPLogger.errorLog;
import static org.javaweb.rasp.commons.utils.StringUtils.isEmpty;
import static org.javaweb.rasp.commons.utils.StringUtils.isNotEmpty;
import static org.javaweb.rasp.commons.utils.URLUtils.urlNormalize;

/**
 * Creator: yz
 * Date: 2019-07-17
 */
public class RASPWhitelist {

	/**
	 * 检测当前请求是否存在于白名单列表
	 *
	 * @param context RASP上下文
	 * @return 是否是白名单请求
	 */
	public static int getWhitelistIndexOfRequest(RASPContext context) {
		try {
			RASPAppProperties properties = context.getAppProperties();

			// 获取白名单列表
			String[] whiteList = properties.getWhitelist();

			if (whiteList.length > 0) {
				String requestURI = context.getRequestPath();

				if (isNotEmpty(requestURI)) {
					// 将请求URL地址转换成标准的路径
					String path = urlNormalize(requestURI);

					// 检测当前的文件路径是否包含在白名单
					for (int i = 0; i < whiteList.length; i++) {
						String url = whiteList[i];
						if (isEmpty(url)) {
							continue;
						}

						int len = url.length();

						// 目录匹配，如：/data/index.jsp -> /data
						if (path.startsWith(url) && (path.length() == len || path.charAt(len) == '/')) {
							return i;
						}
					}
				}
			}
		} catch (Exception e) {
			errorLog("检测白名单功能异常:", e);
		}

		return -1;
	}


	public static String getAttackTypeWhitelist(RASPContext context, int index) {
		if (index == -1) {
			return "";
		}
		try {
			RASPAppProperties properties = context.getAppProperties();

			// 获取白名单列表的攻击类型
			String[] attackTypeList = properties.getWhiteAttackType();

			return attackTypeList[index];
		} catch (Exception e) {
			errorLog("检测白名单功能异常:", e);
		}
		return "";
	}

}
