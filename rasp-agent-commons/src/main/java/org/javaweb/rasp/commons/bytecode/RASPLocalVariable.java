package org.javaweb.rasp.commons.bytecode;

public class RASPLocalVariable extends RASPAttribute {

	private final int index;

	private final String name;

	private final String desc;

	public RASPLocalVariable(int index, String name, String desc) {
		this.index = index;
		this.name = name;
		this.desc = desc;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

}
