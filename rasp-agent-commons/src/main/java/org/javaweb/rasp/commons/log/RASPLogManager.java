package org.javaweb.rasp.commons.log;

import org.javaweb.rasp.commons.config.RASPAppProperties;
import org.javaweb.rasp.commons.config.RASPPropertiesConfiguration;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.addAll;
import static org.javaweb.rasp.commons.config.RASPConfiguration.*;
import static org.javaweb.rasp.commons.constants.RASPConstants.*;
import static org.javaweb.rasp.commons.log.RASPLogger.*;
import static org.javaweb.rasp.commons.utils.EncryptUtils.deContent;
import static org.javaweb.rasp.commons.utils.FileUtils.readLines;
import static org.javaweb.rasp.commons.utils.JsonUtils.toJsonMap;
import static org.javaweb.rasp.loader.AgentConstants.AGENT_NAME;

/**
 * Created by yz on 2017/3/4.
 *
 * @author yz
 */
public class RASPLogManager {

	/**
	 * 获取当前日志文件
	 *
	 * @param type   日志类型
	 * @param config Web应用配置
	 * @return 日志文件
	 */
	public static List<File> getLogFiles(final String type, RASPPropertiesConfiguration<RASPAppProperties> config) {
		final String     appName      = config.getConfigName();
		final List<File> appLogFiles  = new ArrayList<File>();
		final String     loggerPrefix = getLoggerPrefix(type);

		if (ERROR_LOG.equals(type)) {
			addAll(appLogFiles, getLogFiles(RASP_DATABASE_DIRECTORY, "error", type, false));
		} else {
			File[] files = RASP_LOG_DIRECTORY.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isDirectory() && file.getName().equals(appName);
				}
			});

			if (files == null) {
				return appLogFiles;
			}

			for (File file : files) {
				String loggerName = createLoggerName(loggerPrefix, file.getName());
				File[] logFiles   = getLogFiles(file, loggerName, type, true);

				if (logFiles != null) {
					addAll(appLogFiles, logFiles);
				}
			}
		}

		return appLogFiles;
	}

	public static File[] getLogFiles(File app, String loggerName, String logType, boolean splitFile) {
		String fileName = getLoggerFileName(logType);
		File[] logs     = listAppLogFiles(app, fileName);

		if (logs == null || logs.length == 0) {
			return rolloverLog(app, fileName, loggerName, splitFile);
		}

		return logs;
	}

	private static File[] rolloverLog(File app, String fileName, String loggerName, boolean splitFile) {
		File logFile = new File(app, fileName);

		// 如果日志文件不存在，或者日志文件有日志的情况需要刷新Logger文件
		if (!logFile.exists() || logFile.length() > 0) {
			rollover(loggerName, splitFile);

			// 已刷新logger，重新再获取一次日志文件
			return listAppLogFiles(app, fileName);
		}

		return new File[0];
	}

	/**
	 * 根据Logger的文件名获取应用日志，如传入的Logger文件名为：rasp-attack.log，将查找当前目录中是否有以如以
	 * rasp-attack.log为前缀的日志文件，如果存在则返回该日志文件，如：rasp-attack.log.230876160930141.1.txt
	 *
	 * @param app            RASP待读取的应用日志目录
	 * @param loggerFileName logger日志名称，如：rasp-attack.log
	 * @return 返回Logger文件对应的日志文件数组
	 */
	public static File[] listAppLogFiles(File app, final String loggerFileName) {
		return app.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				String name = file.getName();

				if (!name.endsWith(".txt")) {
					return false;
				}

				// 清空字节的日志文件
				if (file.length() == 0) {
					if (file.delete()) {
						MODULES_LOGGER.debug("{} 删除空日志文件：{}，异常！", AGENT_NAME, file);
					}

					return false;
				}

				return name.startsWith(loggerFileName);
			}
		});
	}

	public static List<Map<String, Object>> readLog(File logFile, String appId, String logType) throws IOException {
		try {
			List<Map<String, Object>> logs   = new ArrayList<Map<String, Object>>();
			String                    rc4Key = AGENT_PROPERTIES.getRc4Key();

			if (logFile.exists() && logFile.length() > 0) {
				// 按行读取日志文件
				List<String> lines = readLines(logFile, DEFAULT_ENCODING);

				for (String line : lines) {
					Map<String, Object> map;

					// 攻击日志需要解密，访问日志不需要
					if (ATTACK_LOG.equals(logType)) {
						map = toJsonMap(deContent(line, rc4Key));
					} else {
						map = toJsonMap(line);
					}

					// 更新应用ID
					map.put("app_id", appId);

					logs.add(map);
				}
			} else {
				MODULES_LOGGER.info("{}无法读取日志文件：{}，内容为空!", AGENT_NAME, logFile);
			}

			return logs;
		} finally {
			if (!logFile.delete()) {
				errorLog("{}删除日志文件：{}异常!", AGENT_NAME, logFile);
			}
		}
	}

}
