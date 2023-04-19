package org.javaweb.rasp.commons;

import org.javaweb.rasp.loader.AgentClassLoader;

import java.io.File;

public interface RASPUpgrade {

	void upgrade(File zipFile, AgentClassLoader raspClassLoader);

}
