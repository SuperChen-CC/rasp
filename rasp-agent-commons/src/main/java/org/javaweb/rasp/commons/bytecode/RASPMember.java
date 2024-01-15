package org.javaweb.rasp.commons.bytecode;

import java.util.*;

import static org.javaweb.rasp.commons.utils.ClassUtils.getArgs;

public class RASPMember {

	/**
	 * 访问修饰符
	 */
	public int access;

	/**
	 * 成员变量/方法名称
	 */
	public String name;

	/**
	 * 签名、描述符
	 */
	public String desc;

	/**
	 * attributes属性数量
	 */
	public int attributesCount;

	public final Map<String, List<RASPAttribute>> attributes = new HashMap<String, List<RASPAttribute>>();

	public RASPMember(int access, String name, String desc) {
		this.access = access;
		this.name = name;
		this.desc = desc;
	}

	public RASPMember(int access, String name, String desc, int attributesCount) {
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.attributesCount = attributesCount;
	}

	public int getAccess() {
		return access;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public int getAttributesCount() {
		return attributesCount;
	}

	public void addAttributes(String type, RASPAttribute attribute) {
		Object obj = attributes.get(type);

		if (obj == null) {
			attributes.put(type, new ArrayList<RASPAttribute>());
		}

		attributes.get(type).add(attribute);
	}

	public List<RASPAttribute> getAttributes(String type) {
		if (attributes.containsKey(type)) {
			return attributes.get(type);
		}

		return new ArrayList<RASPAttribute>();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RASPMember) {
			RASPMember member     = (RASPMember) obj;
			String     memberName = member.getName();
			String     memberDesc = getArgs(member.getDesc());

			desc = getArgs(desc);

			if (!(name == null ? memberName == null : name.equals(memberName))) return false;

			return desc == null ? memberDesc == null : desc.equals(memberDesc);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new String[]{name, desc});
	}

}