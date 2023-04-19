package org.javaweb.rasp.commons.bytecode;

public class RASPClass {

	private int accessFlags;

	private int minorVersion;

	private int majorVersion;

	private String thisClass;

	private String superClass;

	private String[] interfaces;

	private RASPMember[] fields;

	private RASPMethod[] methods;

	public int getAccessFlags() {
		return accessFlags;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}

	public void setAccessFlags(int accessFlags) {
		this.accessFlags = accessFlags;
	}

	public String getThisClass() {
		return thisClass;
	}

	public void setThisClass(String thisClass) {
		this.thisClass = thisClass;
	}

	public String getSuperClass() {
		return superClass;
	}

	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}

	public String[] getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(String[] interfaces) {
		this.interfaces = interfaces;
	}

	public RASPMember[] getFields() {
		return fields;
	}

	public void setFields(RASPMember[] fields) {
		this.fields = fields;
	}

	public RASPMethod[] getMethods() {
		return methods;
	}

	public void setMethods(RASPMethod[] methods) {
		this.methods = methods;
	}

	public boolean isInterface() {
		return (accessFlags & 0x0200) != 0;
	}

}