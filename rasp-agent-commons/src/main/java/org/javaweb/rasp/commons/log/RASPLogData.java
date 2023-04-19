package org.javaweb.rasp.commons.log;

import org.javaweb.rasp.commons.logback.classic.Logger;

public class RASPLogData {

	private final String raspLog;

	private final Logger logger;

	/**
	 * 是否加密JSON
	 */
	private final boolean encrypt;

	public RASPLogData(String raspLog, Logger logger, boolean encrypt) {
		this.raspLog = raspLog;
		this.logger = logger;
		this.encrypt = encrypt;
	}

	public String getRaspLog() {
		return raspLog;
	}

	public Logger getLogger() {
		return logger;
	}

	public boolean isEncrypt() {
		return encrypt;
	}

}
