package org.javaweb.rasp.commons;

import com.google.gson.annotations.SerializedName;

import java.io.File;

public class RASPVersion {

	@SerializedName("git.build.version")
	private final String version;

	@SerializedName("git.commit.id.abbrev")
	private final String commitID;

	private transient final File baseDir;

	private transient final File agentJarFile;

	@SerializedName("git.build.time")
	private final String buildTime;

	public RASPVersion(String version, File baseDir, File agentJarFile) {
		this(version, null, baseDir, agentJarFile, null);
	}

	public RASPVersion(String version, String commitID, File baseDir, File agentJarFile, String buildTime) {
		this.version = version;
		this.commitID = commitID;
		this.baseDir = baseDir;
		this.agentJarFile = agentJarFile;
		this.buildTime = buildTime;
	}

	public String getVersion() {
		return version;
	}

	public String getCommitID() {
		return commitID;
	}

	public File getBaseDir() {
		return baseDir;
	}

	public File getAgentJarFile() {
		return agentJarFile;
	}

	public String getBuildTime() {
		return buildTime;
	}

}
