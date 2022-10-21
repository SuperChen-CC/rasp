package org.javaweb.rasp.commons.sync;

import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class RASPThreadSyncConfig {

	/**
	 * 是否是运行中
	 */
	private boolean running;

	/**
	 * 间隔时间
	 */
	private long syncInterval;

	public RASPThreadSyncConfig(long syncInterval, boolean running) {
		this.syncInterval = syncInterval;
		this.running = running;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public long getSyncInterval() {
		return this.syncInterval;
	}

	public void setSyncInterval(long syncInterval) {
		this.syncInterval = syncInterval;
	}

	public abstract void dataSynchronization();

	private final Map<Object, Object> callback = new HashMap<Object, Object>() {
		@Override
		public Object get(Object key) {
			if ("isRunning".equals(key)) {
				return isRunning();
			} else if ("getSyncInterval".equals(key)) {
				return getSyncInterval();
			} else if ("dataSynchronization".equals(key)) {
				dataSynchronization();

				// 同步时需间隔一定的时间
				syncInterval();
			}

			return null;
		}
	};

	private void syncInterval() {
		try {
			if (syncInterval > 0) {
				Thread.sleep(SECONDS.toMillis(getSyncInterval()));
			}
		} catch (InterruptedException ignored) {
		}
	}

	public Map<Object, Object> getCallback() {
		return callback;
	}

}
