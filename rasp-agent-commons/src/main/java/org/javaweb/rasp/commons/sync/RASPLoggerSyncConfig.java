package org.javaweb.rasp.commons.sync;

import org.javaweb.rasp.commons.RASPAgentEnv;
import org.javaweb.rasp.commons.log.RASPLogData;
import org.javaweb.rasp.commons.logback.classic.Logger;

import java.util.concurrent.ArrayBlockingQueue;

import static org.javaweb.rasp.commons.config.RASPConfiguration.AGENT_PROPERTIES;
import static org.javaweb.rasp.commons.utils.EncryptUtils.enContent;

public class RASPLoggerSyncConfig extends RASPThreadSyncConfig {

	public RASPLoggerSyncConfig(long syncInterval, boolean running) {
		super(syncInterval, running);
	}

	/**
	 * 设置内存中最大放置的RASP日志数
	 */
	private static final int MAX_LOG_COUNT = 1000;

	/**
	 * RASP日志队列
	 */
	protected static ArrayBlockingQueue<RASPLogData> raspLogQueue = new ArrayBlockingQueue<RASPLogData>(MAX_LOG_COUNT);

	public static void addRASPLogData(RASPLogData log) {
		if (raspLogQueue.size() < MAX_LOG_COUNT) {
			raspLogQueue.offer(log);
		} else {
			writeLog(log);
		}
	}

	private static void writeLog(RASPLogData logData) {
		Logger logger = logData.getLogger();
		String log    = logData.getRaspLog();

		if (logger != null && log != null) {
			if (logData.isEncrypt()) {
				log = enContent(log, AGENT_PROPERTIES.getRc4Key());
			}

			logger.info(log);
		}
	}

	@Override
	public void dataSynchronization(RASPAgentEnv agentEnv) {
		for (RASPLogData log; (log = raspLogQueue.poll()) != null; ) {
			writeLog(log);
		}
	}

}
