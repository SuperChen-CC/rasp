package org.javaweb.rasp.commons.bytecode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.javaweb.rasp.commons.utils.ClassUtils.getDescriptor;

public class RASPMethod extends RASPMember {

	public RASPMethod(int access, String name, String desc) {
		super(access, name, desc, -1);
	}

	public RASPMethod(int access, String name, String desc, int attributesCount) {
		super(access, name, desc, attributesCount);
	}

	/**
	 * 通过反射的Constructor对象反向生成RASPMethod，该Method不会包含attributes信息
	 *
	 * @param c Constructor 构造器
	 * @return RASPMethod
	 */
	public static RASPMethod getMethod(Constructor<?> c) {
		return new RASPMethod(c.getModifiers(), "<init>", getDescriptor(c.getParameterTypes()));
	}

	public static RASPMethod getMethod(Method method) {
		return new RASPMethod(method.getModifiers(), method.getName(), getDescriptor(method.getParameterTypes()));
	}

}