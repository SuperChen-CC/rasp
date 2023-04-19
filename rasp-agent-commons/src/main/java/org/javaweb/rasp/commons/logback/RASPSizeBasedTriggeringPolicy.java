package org.javaweb.rasp.commons.logback;

import org.javaweb.rasp.commons.logback.core.rolling.TriggeringPolicyBase;
import org.javaweb.rasp.commons.logback.core.util.DefaultInvocationGate;
import org.javaweb.rasp.commons.logback.core.util.FileSize;
import org.javaweb.rasp.commons.logback.core.util.InvocationGate;

import java.io.File;

public class RASPSizeBasedTriggeringPolicy<E> extends TriggeringPolicyBase<E> {

	/**
	 * The default maximum file size 10 MB
	 */
	private FileSize maxFileSize = new FileSize(10 * 1024 * 1024);

	private final InvocationGate invocationGate = new DefaultInvocationGate();

	public RASPSizeBasedTriggeringPolicy() {
	}

	public boolean isTriggeringEvent(final File activeFile, final E event) {
		long now = System.currentTimeMillis();
		if (invocationGate.isTooSoon(now))
			return false;

		return activeFile.length() >= maxFileSize.getSize();
	}

	public void setMaxFileSize(FileSize aMaxFileSize) {
		this.maxFileSize = aMaxFileSize;
	}

}
