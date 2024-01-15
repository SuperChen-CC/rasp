package org.javaweb.rasp.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static org.javaweb.rasp.commons.utils.IOUtils.closeQuietly;
import static org.javaweb.rasp.commons.utils.IOUtils.toByteArray;

/**
 * Created by yz on 2016-05-27.
 */
public class ClassUtils {

	/**
	 * 获取用于ASM调用的类名称
	 *
	 * @param clazz 类对象
	 * @return ASM格式的Java类名称
	 */
	public static String toAsmClassName(Class<?> clazz) {
		return clazz.getName().replace(".", "/");
	}

	/**
	 * 获取用于ASM调用的类名称
	 *
	 * @param className 类名
	 * @return ASM格式的Java类名称
	 */
	public static String toAsmClassName(String className) {
		return className.replace(".", "/");
	}

	/**
	 * 转换成Java内部命名方式
	 *
	 * @param className 类名
	 * @return Java类格式的类名称
	 */
	public static String toJavaName(String className) {
		return className != null ? className.replace("/", ".") : null;
	}

	public static String getArgs(final String desc) {
		if (desc == null) return null;

		if (desc.contains("(") && desc.contains(")")) {
			return desc.substring(1, desc.indexOf(")"));
		}

		return desc;
	}

	/**
	 * 获取参数描述符,接收参数类型的全类名，如果是参数是数组类型的那么直接在类型后面加上一对"[]"就可以了,
	 * 如"java.lang.String[]",如果是基础类型直接写就行了，如参数类型是int,那么直接传入:"int"就行了。
	 * 需要特别注意的是类名一定不能写错,"[]"也一定不能加错，否则无法正常匹配。
	 *
	 * @param classes 参数类型
	 * @return 方法描述符
	 */
	public static String getDescriptor(final String... classes) {
		StringBuilder sb = new StringBuilder();

		for (String name : classes) {
			// 替换掉多余的空白符、替换"."为"/"
			name = toAsmClassName(name.replaceAll("\\s+", ""));

			// 统计数组[]出现次数
			int length = name.split("\\[]", -1).length;

			for (int i = 0; i < length - 1; i++) {
				sb.append("[");
			}

			// 移除所有[]
			String className = ClassUtils.toAsmClassName(name.replace("[]", ""));

			if (Byte.TYPE.getName().equals(className)) {
				sb.append('B');
			} else if (Boolean.TYPE.getName().equals(className)) {
				sb.append('Z');
			} else if (Short.TYPE.getName().equals(className)) {
				sb.append('S');
			} else if (Character.TYPE.getName().equals(className)) {
				sb.append('C');
			} else if (Integer.TYPE.getName().equals(className)) {
				sb.append('I');
			} else if (Long.TYPE.getName().equals(className)) {
				sb.append('J');
			} else if (Double.TYPE.getName().equals(className)) {
				sb.append('D');
			} else if (Float.TYPE.getName().equals(className)) {
				sb.append('F');
			} else if (Void.TYPE.getName().equals(className)) {
				sb.append('V');
			} else {
				sb.append("L").append(className).append(";");
			}
		}

		return sb.toString();
	}

	/**
	 * Appends the descriptor of the given class to the given string builder.
	 *
	 * @param clazz         the class whose descriptor must be computed.
	 * @param stringBuilder the string builder to which the descriptor must be appended.
	 */
	private static void appendDescriptor(final Class<?> clazz, final StringBuilder stringBuilder) {
		Class<?> currentClass = clazz;

		while (currentClass.isArray()) {
			stringBuilder.append('[');
			currentClass = currentClass.getComponentType();
		}

		if (currentClass.isPrimitive()) {
			char descriptor;
			if (currentClass == Integer.TYPE) {
				descriptor = 'I';
			} else if (currentClass == Void.TYPE) {
				descriptor = 'V';
			} else if (currentClass == Boolean.TYPE) {
				descriptor = 'Z';
			} else if (currentClass == Byte.TYPE) {
				descriptor = 'B';
			} else if (currentClass == Character.TYPE) {
				descriptor = 'C';
			} else if (currentClass == Short.TYPE) {
				descriptor = 'S';
			} else if (currentClass == Double.TYPE) {
				descriptor = 'D';
			} else if (currentClass == Float.TYPE) {
				descriptor = 'F';
			} else if (currentClass == Long.TYPE) {
				descriptor = 'J';
			} else {
				throw new AssertionError();
			}
			stringBuilder.append(descriptor);
		} else {
			stringBuilder.append('L').append(toAsmClassName(currentClass.getName())).append(';');
		}
	}

	/**
	 * 获取类类型描述符
	 *
	 * @param classes 参数类型
	 * @return 方法描述符
	 */
	public static String getDescriptor(final Class<?>... classes) {
		StringBuilder buf = new StringBuilder();

		for (Class<?> clazz : classes) {
			appendDescriptor(clazz, buf);
		}

		return buf.toString();
	}

	public static Map<String, String> loadProperties(File file) throws IOException {
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(file);
			Properties p = new Properties();
			p.load(fis);

			return propertiesToMap(p);
		} finally {
			if (fis != null)
				closeQuietly(fis);
		}
	}

	/**
	 * Properties文件转Map对象
	 *
	 * @param properties Properties文件
	 * @return 转换后的Map
	 */
	public static Map<String, String> propertiesToMap(Properties properties) {
		Map<String, String> configMap = new HashMap<String, String>();

		for (String key : properties.stringPropertyNames()) {
			configMap.put(key, properties.getProperty(key));
		}

		return configMap;
	}

	public static Map<String, String> getFilePropertiesMap(File file) {
		FileInputStream fis = null;

		try {
			Properties p = new Properties();
			fis = new FileInputStream(file);

			p.load(fis);
			return propertiesToMap(p);
		} catch (IOException ignored) {
		} finally {
			closeQuietly(fis);
		}

		return null;
	}

	/**
	 * 读取指定jar中的Properties文件
	 *
	 * @param jarFile jar
	 * @param res     资源路径
	 * @return Map
	 */
	public static Map<String, String> getJarPropertiesMap(File jarFile, String res) {
		FileInputStream fis = null;
		JarInputStream  zis = null;

		try {
			Properties p = new Properties();
			fis = new FileInputStream(jarFile);
			zis = new JarInputStream(fis);

			JarEntry entry;

			while ((entry = zis.getNextJarEntry()) != null) {
				String name = entry.getName();

				if (name.equals(res)) {
					p.load(zis);
					return propertiesToMap(p);
				}
			}
		} catch (IOException ignored) {
		} finally {
			closeQuietly(zis);
			closeQuietly(fis);
		}

		return null;
	}

	public static byte[] getClassBytes(Class<?> clazz) {
		return getClassBytes(clazz.getName(), clazz.getClassLoader());
	}

	/**
	 * 查找类对象，获取类字节码
	 *
	 * @param className   类名
	 * @param classLoader 类加载器
	 * @return 类字节码数组
	 */
	public static byte[] getClassBytes(String className, ClassLoader classLoader) {
		InputStream in = null;

		try {
			if (className.startsWith("[")) {
				return null;
			}

			String classRes = toAsmClassName(className) + ".class";

			in = getSystemResourceAsStream(classRes);

			if (classLoader != null && in == null) {
				in = classLoader.getResourceAsStream(classRes);
			}

			if (in != null) {
				return toByteArray(in);
			}

			return null;
		} catch (IOException e) {
			return null;
		} finally {
			closeQuietly(in);
		}
	}

}
