package org.javaweb.rasp.commons.config;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.javaweb.rasp.commons.constants.RASPConstants.DEFAULT_ENCODING;
import static org.javaweb.rasp.commons.log.RASPLogger.errorLog;
import static org.javaweb.rasp.commons.utils.FileUtils.*;
import static org.javaweb.rasp.commons.utils.IOUtils.closeQuietly;

public class RASPPropertiesConfiguration<T extends RASPProperties> {

	/**
	 * 配置文件
	 */
	private final File configFile;

	/**
	 * 配置对象
	 */
	private final T raspProperties;

	/**
	 * 配置文件内容
	 */
	private String content;

	private static final ReentrantLock FILE_LOCK = new ReentrantLock();

	/**
	 * 默认文件锁时间：10s
	 */
	private final long defaultLockTimeout = TimeUnit.SECONDS.toMillis(10);

	public RASPPropertiesConfiguration(File configFile, Class<T> configClass) throws Exception {
		this.configFile = configFile;
		this.raspProperties = configClass.newInstance();

		raspProperties.reloadConfig(readProperties());
	}

	public File getConfigFile() {
		return configFile;
	}

	public T getRaspProperties() {
		return raspProperties;
	}

	public String getConfigName() {
		String appConfigName = configFile.getName();

		return appConfigName.substring(0, appConfigName.lastIndexOf("."));
	}

	/**
	 * 修改配置文件属性
	 *
	 * @param configMap 需要修改的Map
	 * @throws IOException 修改文件时异常
	 */
	public synchronized void setProperty(Map<String, String> configMap) throws IOException {
		// 修改属性
		writeProperties(configMap);

		raspProperties.reloadConfig(readProperties());
	}

	/**
	 * 使用非标准的方式解析Properties文件配置，仅能应用于RASP
	 *
	 * @return 解析后的配置文件Map
	 * @throws IOException IO异常
	 */
	public synchronized RASPConfigMap<String, Object> readProperties() throws IOException {
		RASPConfigMap<String, Object> configMap = new RASPConfigMap<String, Object>();

		try {
			FILE_LOCK.lock();

			// 配置文件丢失，自动复制内存中的配置修复
			if (!configFile.exists()) {
				errorLog("配置文件：" + configFile.getAbsolutePath() + "不存在！");

				writeStringToFile(configFile, content);
			}

			content = readFileToString(configFile, DEFAULT_ENCODING);

			String         line;
			BufferedReader reader = new BufferedReader(new StringReader(content));

			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}

				char[] chars    = line.toCharArray();
				int    valIndex = 0;

				// 忽略被"#"或"!"注释的行
				if ('#' == chars[0] || '!' == chars[0]) {
					continue;
				}

				// 查找"="或":"所在的位置，用于切分key/value
				for (char chr : chars) {
					if (chr == '=' || chr == ':') {
						break;
					}

					valIndex++;
				}

				// 解析参数名称和参数值
				if (valIndex > 0 && valIndex != chars.length) {
					String key = new String(chars, 0, valIndex).trim();

					// 替换参数值中的"\"，将两个"\\"替换成一个"\"
					String value = loadConvert(new String(
							chars, valIndex + 1, chars.length - valIndex - 1
					)).trim();

					if (!key.isEmpty()) {
						configMap.put(key, value);
					}
				}
			}
		} finally {
			FILE_LOCK.unlock();
		}

		return configMap;
	}

	/**
	 * 使用非标准的方式修改Properties文件配置，仅能应用于RASP
	 *
	 * @param map 需要修改的key/value集合
	 * @throws IOException IO异常
	 */
	public synchronized void writeProperties(Map<String, String> map) throws IOException {
		if (map.isEmpty()) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		List<String>  lines;

		try {
			FILE_LOCK.lock();
			lines = readLines(configFile, DEFAULT_ENCODING);

			for (String line : lines) {
				if (line.isEmpty()) {
					continue;
				}

				char[] chars    = line.toCharArray();
				int    valIndex = 0;

				for (char chr : chars) {
					if ('#' == chars[0] || '!' == chars[0]) {
						sb.append(line).append("\n");
						break;
					}

					if (chr == '=' || chr == ':') {
						break;
					}

					valIndex++;
				}

				if (valIndex > 0 && valIndex != chars.length) {
					String key   = new String(chars, 0, valIndex).trim();
					String value = new String(chars, valIndex + 1, chars.length - valIndex - 1).trim();

					if (!key.isEmpty() && map.containsKey(key)) {
						value = saveConvert(map.get(key));
					}

					sb.append(key).append("=").append(value).append("\n");
				}
			}

			// 配置文件如果内容为空（配置文件丢失），复制内存中的配置到文件
			if (sb.length() > 0) {
				modifyConfigFile(sb.toString());
			} else {
				errorLog("配置文件：" + configFile.getAbsolutePath() + "同步异常，文件内容不能修改为空！");

				writeStringToFile(configFile, content);
			}
		} finally {
			FILE_LOCK.unlock();
		}
	}

	private void modifyConfigFile(String content) throws IOException {
		FileLock         lock = null;
		FileOutputStream fos  = null;

		try {
			fos = new FileOutputStream(configFile);
			FileChannel channel = fos.getChannel();

			lock = channel.lock();

			// 创建文件锁释放线程，超时自动释放
			setLockTimeout(lock);

			fos.write(content.getBytes(DEFAULT_ENCODING));
			fos.flush();
		} catch (IOException e) {
			errorLog("配置文件：" + configFile + "同步异常！", e);
		} finally {
			if (lock != null) lock.release();

			closeQuietly(fos);
		}
	}

	private void setLockTimeout(final FileLock lock) {
		final Timer timer = new Timer();

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (lock != null && lock.isValid()) {
					// 自动释放锁
					try {
						lock.release();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}

				// 取消计时器任务
				timer.cancel();
			}
		}, defaultLockTimeout);
	}

	/**
	 * 读取properties的时候需要转义属性值
	 *
	 * @param str 属性值
	 * @return 解析之后的属性值
	 */
	private static String loadConvert(String str) {
		if (str == null || str.isEmpty()) return str;

		return str.replace("\\\\", "\\");
	}

	/**
	 * 保存properties的时候转义属性值
	 *
	 * @param str 属性值
	 * @return 转义后的属性值
	 */
	private static String saveConvert(String str) {
		if (str == null || str.isEmpty()) return "";

		return str.replace("\\", "\\\\");
	}

}