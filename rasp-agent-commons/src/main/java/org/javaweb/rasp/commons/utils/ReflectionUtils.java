package org.javaweb.rasp.commons.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.javaweb.rasp.commons.config.RASPConfiguration.AGENT_LOGGER;
import static org.javaweb.rasp.loader.AgentConstants.AGENT_NAME;

public final class ReflectionUtils {

	public static final Class<?>[] EMPTY_ARGS = new Class<?>[0];

	public static final Class<?>[] BOOLEAN_CLASS_ARG = new Class[]{boolean.class};

	public static final Class<?>[] STRING_CLASS_ARG = new Class[]{String.class};

	public static final Class<?>[] STRING_STRING_CLASS_ARG = new Class[]{String.class, String.class};

	public static final Class<?>[] INT_STRING_CLASS_ARG = new Class[]{int.class, String.class};

	public static final Class<?>[] STRING_OBJECT_CLASS_ARG = new Class[]{String.class, Object.class};

	public static final Class<?>[] INT_CLASS_ARG = new Class[]{int.class};

	public static final Class<?>[] BYTE_ARRAY_CLASS_ARG = new Class[]{byte[].class};

	public static final Class<?>[] STREAM_CLASS_ARG = new Class[]{byte[].class, int.class, int.class};

	private static final Map<Integer, Method> CACHE_CLASS_METHOD_MAP = new ConcurrentHashMap<Integer, Method>();

	private static final Map<Integer, Field> CACHE_CLASS_FIELD_MAP = new ConcurrentHashMap<Integer, Field>();

	public static String methodToString(String className, String method, Class<?>... classes) {
		StringBuilder sb = new StringBuilder();

		if (className != null) {
			sb.append(className).append('.');
		}

		return sb.append(method).append(methodToString(classes)).toString();
	}

	public static String methodToString(Class<?>... classes) {
		StringBuilder sb = new StringBuilder("(");

		for (int i = 0; i < classes.length; i++) {
			if (i > 0) {
				sb.append(',');
			}

			sb.append(classes[i].getName());
		}

		return sb.append(')').toString();
	}

	public static String getMethodHashcode(Object className, String method) {
		StringBuilder sb = new StringBuilder();

		if (className != null) {
			sb.append(className);
		}

		if (method != null) {
			if (sb.length() > 0) {
				sb.append('#');
			}

			sb.append(method);
		}

		return sb.toString();
	}

	public static int getMethodHashcode(Object className, String method, Class<?>... typeClasses) {
		StringBuilder sb = new StringBuilder();
		sb.append(getMethodHashcode(className, method));

		if (typeClasses.length > 0) {
			sb.append('(');

			for (int i = 0; i < typeClasses.length; i++) {
				if (i > 0) {
					sb.append(',');
				}

				sb.append(typeClasses[i].getName());
			}

			sb.append(')');
		}

		return sb.hashCode();
	}

	public static int getFieldHashcode(Object className, String field) {
		int hashcode = 0;

		if (className != null) {
			hashcode = className.hashCode();
		}

		if (field != null) {
			hashcode += field.hashCode();
		}

		return hashcode;
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... argTypes) throws NoSuchMethodException {
		int    hashCode = getMethodHashcode(clazz, name, argTypes);
		Method method   = CACHE_CLASS_METHOD_MAP.get(hashCode);

		if (method != null) {
			return method;
		}

		while (clazz != Object.class) {
			try {
				method = clazz.getDeclaredMethod(name, argTypes);
				break;
			} catch (NoSuchMethodException e) {
				clazz = clazz.getSuperclass();
			}
		}

		if (method == null) {
			throw new NoSuchMethodException(name);
		} else {
			method.setAccessible(true);

			// 缓存类方法
			CACHE_CLASS_METHOD_MAP.put(hashCode, method);
		}

		return method;
	}

	public static Object invokeMethod(Object instance, String name, Class<?>[] argTypes, Object... args)
			throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

		Method method = getMethod(instance.getClass(), name, argTypes);

		return method.invoke(instance, args);
	}

	public static <T> T invokeProxyMethod(Object instance, String name) {
		return invokeProxyMethod(instance, name, EMPTY_ARGS);
	}

	public static <T> T invokeProxyMethod(Object instance, String name, Class<?>[] argTypes, Object... args) {
		return invokeMethodProxy(instance, name, argTypes, args);
	}

	public static Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
		int   hashCode = getFieldHashcode(clazz, name);
		Field field    = CACHE_CLASS_FIELD_MAP.get(hashCode);

		if (field != null) {
			return field;
		}

		while (clazz != Object.class) {
			try {
				field = clazz.getDeclaredField(name);
				break;
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}

		if (field == null) {
			throw new NoSuchFieldException(name);
		} else {
			field.setAccessible(true);

			// 缓存类方法
			CACHE_CLASS_FIELD_MAP.put(hashCode, field);
		}

		return field;
	}

	public static Object invokeStaticField(Class<?> clazz, String name)
			throws NoSuchFieldException, IllegalAccessException {

		Field field = getField(clazz, name);

		return field.get(null);
	}

	public static Object invokeField(Object instance, String name)
			throws NoSuchFieldException, IllegalAccessException {

		Field field = getField(instance.getClass(), name);

		return field.get(instance);
	}

	public static <T> T invokeStaticMethod(Class<?> clazz, String method, Class<?>[] types, Object... args) {
		try {
			return invokeStaticMethod(getMethod(clazz, method, types), args);
		} catch (NoSuchMethodException e) {
			if (AGENT_LOGGER.isDebugEnabled()) {
				AGENT_LOGGER.error(AGENT_NAME + "反射调用" + clazz + "#" + method + "异常，该方法不存在！");
			}
		}

		return null;
	}

	public static <T> T invokeStaticMethod(Method method, Object... args) {
		try {
			if (method != null) {
				return (T) method.invoke(null, args);
			}
		} catch (Exception e) {
			if (AGENT_LOGGER.isDebugEnabled()) {
				AGENT_LOGGER.error(AGENT_NAME + "反射调用" + method + "异常：{}", e);
			}
		}

		return null;
	}

	public static <T> T invokeMethodProxy(Object instance, String name) {
		return invokeMethodProxy(instance, name, new Class[0]);
	}

	public static <T> T invokeMethodProxy(Object instance, String name, Class<?>[] argTypes, Object... args) {
		try {
			return (T) invokeMethod(instance, name, argTypes, args);
		} catch (Exception e) {
			if (AGENT_LOGGER.isDebugEnabled()) {
				AGENT_LOGGER.error(AGENT_NAME + "反射调用" + instance.getClass().getName() + "#" + name + "异常", e);
			}

			return null;
		}
	}

	public static <T> T invokeFieldProxy(Object instance, String name) {
		try {
			return (T) invokeField(instance, name);
		} catch (Exception e) {
			if (AGENT_LOGGER.isDebugEnabled()) {
				AGENT_LOGGER.error(AGENT_NAME + "反射调用" + instance.getClass().getName() + "." + name + "异常", e);
			}

			return null;
		}
	}

}
