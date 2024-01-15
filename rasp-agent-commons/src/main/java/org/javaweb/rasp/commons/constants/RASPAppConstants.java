package org.javaweb.rasp.commons.constants;

public class RASPAppConstants {

	/**
	 * 应用ID
	 */
	public static final String APP_ID = "app_id";

	/**
	 * 默认的未初始化的应用ID
	 */
	public static final String DEFAULT_APP_ID = "{app_id}";

	/**
	 * 开启的RASP 防御模块
	 */
	public static final String MODULES_OPEN = "modules.open";

	/**
	 * 防御模块状态：开启/关闭检测，如果配置为false则会关闭所有防御模块检测
	 */
	public static final String MODULE_DEFENSE = "module.defense";

	/**
	 * RASP模块检测熔断时间（毫秒）, <=0 表示不限制
	 */
	public static final String RASP_PROCESS_TIMEOUT = "rasp_process_timeout";

	/**
	 * 是否是静默模式: true、false
	 */
	public static final String SILENT = "silent";

	/**
	 * 是否防御RASP检测到的漏洞，默认不防御只记录日志
	 */
	public static final String DEFENSE_AGAINST_VUL = "defense_against_vul";

	/**
	 * IP黑名单列表
	 */
	public static final String IP_BLACKLIST = "ip.blacklist";

	/**
	 * URL黑名单列表
	 */
	public static final String URL_BLACKLIST = "url.blacklist";

	/**
	 * IP白名单列表
	 */
	public static final String IP_WHITELIST = "ip.whitelist";

	/**
	 * 请求头白名单
	 */
	public static final String HEADER_WHITELIST = "header.whitelist";

	/**
	 * 白名单列表
	 */
	public static final String WHITELIST = "whitelist";

	/**
	 * 补丁列表
	 */
	public static final String PATCH_LIST = "patch_list";

	/**
	 * 是否启用Servlet输入输出流Hook
	 */
	public static final String SERVLET_STREAM_HOOK = "servlet_stream";

	/**
	 * 最大缓存的Servlet输入输出流大小，默认10MB
	 */
	public static final String SERVLET_STREAM_MAX_CACHE_SIZE = "servlet_stream_max_cache_size";

}
