package org.javaweb.rasp.commons.context;

import org.javaweb.rasp.commons.MethodHookEvent;
import org.javaweb.rasp.commons.attack.RASPAttackInfo;
import org.javaweb.rasp.commons.cache.RASPRequestCached;
import org.javaweb.rasp.commons.config.RASPAppProperties;
import org.javaweb.rasp.commons.config.RASPConfiguration;
import org.javaweb.rasp.commons.config.RASPPropertiesConfiguration;
import org.javaweb.rasp.commons.decoder.RASPDataDecoder;
import org.javaweb.rasp.commons.log.RASPAccessLog;
import org.javaweb.rasp.commons.log.RASPAttackLog;
import org.javaweb.rasp.commons.log.RASPLogData;
import org.javaweb.rasp.commons.log.RASPLogger;
import org.javaweb.rasp.commons.logback.classic.Logger;

import java.io.Closeable;
import java.io.File;
import java.rasp.proxy.loader.RASPModuleType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.nanoTime;
import static org.javaweb.rasp.commons.config.RASPConfiguration.*;
import static org.javaweb.rasp.commons.constants.RASPConstants.*;
import static org.javaweb.rasp.commons.log.RASPLogger.*;
import static org.javaweb.rasp.commons.logback.classic.Level.INFO;
import static org.javaweb.rasp.commons.sync.RASPLoggerSyncConfig.addRASPLogData;
import static org.javaweb.rasp.commons.utils.ArrayUtils.arrayContains;
import static org.javaweb.rasp.commons.utils.JsonUtils.toJson;
import static org.javaweb.rasp.commons.utils.URLUtils.getStandardContextPath;
import static org.javaweb.rasp.loader.AgentConstants.AGENT_NAME;

public abstract class RASPContext implements Closeable {

	/**
	 * RASP 缓存的属性对象
	 */
	protected final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

	/**
	 * 静默模式
	 */
	protected final boolean silent;

	/**
	 * 模块防御状态
	 */
	protected boolean moduleDefense;

	/**
	 * 是否反序列化
	 */
	private boolean deserialization;

	/**
	 * Web应用Context名称
	 */
	protected final String contextPath;

	/**
	 * 是否已经阻断了请求
	 */
	protected boolean blockedRequest;

	/**
	 * 上下文是否已关闭
	 */
	protected boolean contextClosed = false;

	/**
	 * 请求开始的纳秒
	 */
	protected final long requestStartNanoTime;

	/**
	 * 缓存的Servlet、Filter类实例
	 */
	protected final Object cacheClass;

	/**
	 * 方法Hook事件
	 */
	protected MethodHookEvent event;

	/**
	 * Web应用Context名称，自动替换"/"为"_"，如："/console/ibm/"返回"console_ibm"
	 */
	protected String contextName;

	protected RASPRequestCached cachedRequest;

	/**
	 * RASP 应用配置对象
	 */
	protected final RASPPropertiesConfiguration<RASPAppProperties> applicationConfig;

	/**
	 * RASP 应用配置
	 */
	protected final RASPAppProperties appProperties;

	/**
	 * 记录当前context发生的攻击列表
	 */
	protected final Set<RASPModuleType> raspAttackTypeList = new HashSet<RASPModuleType>();

	public RASPContext(MethodHookEvent event) {
		this(event, "/ROOT");
	}

	public RASPContext(MethodHookEvent event, String contextPath) {
		this.requestStartNanoTime = nanoTime();
		this.event = event;
		this.contextPath = getStandardContextPath(contextPath);
		this.cacheClass = event.getThisObject();
		this.cachedRequest = new RASPRequestCached();

		// 获取Web应用配置，第一次请求的时候会比较耗时，因为初始化日志对象和配置文件
		this.applicationConfig = RASPConfiguration.getApplicationConfig(this);
		this.appProperties = applicationConfig.getRaspProperties();
		this.silent = appProperties.isSilent();
		this.moduleDefense = appProperties.isModuleDefense();
	}

	public Object setAttribute(String name, Object value) {
		return attributes.put(name, value);
	}

	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public abstract String getRequestIP();

	public abstract String getServerIP();

	public abstract int getServerPort();

	public abstract String getRequestPath();

	/**
	 * 请求的URL是否是白名单
	 *
	 * @return 是否是白名单
	 */
	public abstract boolean isWhitelist();

	public abstract void blockRequest(RASPAttackInfo attack);

	/**
	 * 添加攻击信息
	 *
	 * @param attack 攻击对象
	 */
	public void addAttackInfo(RASPAttackInfo attack) {
		// 同一次请求中的同一个类型的攻击只记录一次
		if (raspAttackTypeList.contains(attack.getRaspModuleType())) {
			return;
		}

		try {
			// 阻断Http请求
			if (!blockedRequest) {
				blockRequest(attack);
			}
		} catch (Exception e) {
			AGENT_LOGGER.error(AGENT_NAME + "阻断请求异常：" + e, e);
		}

		// 记录日志
		addAttackLog(attack);
	}

	/**
	 * 动态获取当前Web应用的Logger
	 *
	 * @param fileName     文件名
	 * @param loggerPrefix logger后缀
	 * @return 当前Web应用Logger
	 */
	public Logger getAppLogger(String fileName, String loggerPrefix) {
		String fileSize = AGENT_PROPERTIES.getLogBufferSize();

		// 初始化contextName
		String contextName = getContextName();

		// 生成当前应用的Logger名称
		String loggerName = createLoggerName(loggerPrefix, contextName);

		// 检测Logger是否已初始化，无需重复创建logger（会导致日志生成重复记录）
		if (!hasLogger(loggerName)) {
			// RASP日志目录
			File logDir = new File(RASP_LOG_DIRECTORY, contextName);

			if (!logDir.exists() && !logDir.mkdirs()) {
				AGENT_LOGGER.error("初始化{}日志对象失败，无法创建目录：{}", contextName, logDir);
			}

			return createRASPLogger(loggerName, new File(logDir, fileName), INFO, "%msg%n", fileSize);
		}

		return RASPLogger.getLogger(loggerName);
	}

	/**
	 * 获取缓存Http请求(Servlet、Filter)入口类对象
	 *
	 * @return 缓存Http请求入口的类实例
	 */
	public Object getCacheClass() {
		return cacheClass;
	}

	/**
	 * 是否是静默模式
	 *
	 * @return 返回true，如果是静默模式
	 */
	public boolean isSilent() {
		return silent;
	}

	/**
	 * 获取应用的上下文路径
	 *
	 * @return contextPath
	 */
	public final String getContextPath() {
		return contextPath;
	}

	/**
	 * RASP上下文是否已标记为关闭
	 *
	 * @return RASP上下文状态
	 */
	public boolean isContextClosed() {
		return contextClosed;
	}

	/**
	 * 获取当前context下存储的攻击详情
	 *
	 * @return 攻击集合
	 */
	public Set<RASPModuleType> getRaspAttackTypeList() {
		return raspAttackTypeList;
	}

	/**
	 * 检查请求是否需要经过安全模块处理：1. 是否启用RASP模块检测功能；2. 是否是白名单；3. 待检测的模块是否开启；4. 检测是否触发熔断；
	 *
	 * @param moduleType 模块类型
	 * @return 是否需要过滤
	 */
	public boolean mustFilter(RASPModuleType moduleType) {
		// 检测是否启用RASP模块检测功能
		if (!this.isModuleDefense()) {
			return false;
		}

		// 检测是否是白名单URL
		if (isWhitelist()) {
			return false;
		}

		RASPAppProperties appProperties = getAppProperties();

		// 待检测的模块是否开启
		if (!arrayContains(appProperties.getOpenModules(), moduleType.hashCode())) {
			return false;
		}

		// 计算RASP已消耗的时间（ns -> ms）
		long timeInterval = (nanoTime() - getRequestStartNanoTime()) / 1000000;

		// RASP检测时间熔断配置
		int timeout = appProperties.getRaspProcessTimeout();

		// 检测RASP模块处理时间是否触发熔断（ms）
		if (timeout > 0 && timeInterval > timeout) {
			// 触发RASP熔断机制后禁用RASP模块检测
			this.setModuleDefense(false);

			return false;
		}

		return true;
	}

	/**
	 * 获取应用的Context名称，自动替换"/"为"_"，如："/console/ibm/"返回"console_ibm"
	 *
	 * @return Context名称
	 */
	public String getContextName() {
		if (contextName == null && getContextPath() != null) {
			this.contextName = getContextPath().substring(1).replace("/", "_");
		}

		return contextName;
	}

	/**
	 * RASP 处理请求的开始时间戳
	 *
	 * @return 请求开始时间戳
	 */
	public long getRequestStartNanoTime() {
		return requestStartNanoTime;
	}

	/**
	 * 获取RASP攻击日志Logger对象
	 *
	 * @return 攻击日志Logger对象
	 */
	public Logger initAttackLogger() {
		return getAppLogger(ATTACK_LOG_FILE_NAME, ATTACK_LOGGER_PREFIX);
	}

	/**
	 * 获取RASP访问日志Logger对象
	 *
	 * @return 访问日志Logger对象
	 */
	public Logger initAccessLogger() {
		return getAppLogger(ACCESS_LOG_FILE_NAME, ACCESS_LOGGER_PREFIX);
	}

	public void initAppLogger() {
		// 初始化访问日志
		initAccessLogger();

		// 初始化攻击日志
		initAttackLogger();
	}

	public void addAttackLog(RASPAttackInfo attack) {
		if (attack == null) {
			return;
		}

		try {
			// 缓存攻击类型，防止重复记录
			raspAttackTypeList.add(attack.getRaspModuleType());

			Logger        logger    = initAttackLogger();
			RASPAttackLog attackLog = createAttackLog(attack);

			// 记录攻击请求
			if (attackLog != null) {
				// 记录攻击日志
				addRASPLogData(new RASPLogData(toJson(attackLog), logger, true));
			}
		} catch (Exception e) {
			AGENT_LOGGER.error(AGENT_NAME + "写入攻击日志异常：" + e, e);
		}
	}

	public void addAccessLog() {
		// 记录访问日志（必须开启Servlet输入流Hook）
		if (appProperties.isServletStreamHook()) {
			try {
				RASPAccessLog accessLog = createAccessLog();

				if (accessLog != null) {
					// 获取访问日志Logger对象
					Logger accessLogger = initAccessLogger();

					// 记录访问日志
					addRASPLogData(new RASPLogData(toJson(accessLog), accessLogger, false));
				}
			} catch (Exception e) {
				AGENT_LOGGER.error(AGENT_NAME + "写入访问日志异常：" + e, e);
			}
		}
	}

	public RASPAttackLog createAttackLog(RASPAttackInfo attackInfo) {
		return new RASPAttackLog(this, attackInfo);
	}

	public RASPAccessLog createAccessLog() {
		return new RASPAccessLog(this);
	}

	/**
	 * 获取Web应用配置对象
	 *
	 * @return Web应用配置对象
	 */
	public RASPPropertiesConfiguration<RASPAppProperties> getApplicationConfig() {
		return applicationConfig;
	}

	public RASPAppProperties getAppProperties() {
		return appProperties;
	}

	/**
	 * 获取模块防御状态
	 *
	 * @return 是否加载防御模块检测
	 */
	public boolean isModuleDefense() {
		return moduleDefense;
	}

	public void setModuleDefense(boolean moduleDefense) {
		this.moduleDefense = moduleDefense;
	}

	/**
	 * 设置反序列化
	 */
	public void setDeserializationStatus() {
		deserialization = true;
	}

	/**
	 * 当前请求中是否包含反序列化行为
	 *
	 * @return 是否反序列化
	 */
	public boolean isDeserialization() {
		return deserialization;
	}

	/**
	 * 获取RASP缓存的请求对象
	 *
	 * @return 缓存请求对象
	 */
	public RASPRequestCached getCachedRequest() {
		return cachedRequest;
	}

	public RASPDataDecoder getRASPDecoder() {
		return event.getDecoder();
	}

	public void close() {
		// 关闭请求缓存数据
		this.getCachedRequest().close();

		// 标记RASP上下文已关闭
		this.contextClosed = true;
	}

}
