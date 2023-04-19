package org.javaweb.rasp.commons.bytecode;


import org.javaweb.rasp.commons.utils.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class RASPClassReader {

	private static final int CONSTANT_Utf8 = 1;

	private static final int CONSTANT_Integer = 3;

	private static final int CONSTANT_Float = 4;

	private static final int CONSTANT_Long = 5;

	private static final int CONSTANT_Double = 6;

	private static final int CONSTANT_Class = 7;

	private static final int CONSTANT_String = 8;

	private static final int CONSTANT_Fieldref = 9;

	private static final int CONSTANT_Methodref = 10;

	private static final int CONSTANT_InterfaceMethodref = 11;

	private static final int CONSTANT_NameAndType = 12;

	private static final int CONSTANT_MethodHandle = 15;

	private static final int CONSTANT_MethodType = 16;

	private static final int CONSTANT_Dynamic = 17;

	private static final int CONSTANT_InvokeDynamic = 18;

	private static final int CONSTANT_Module = 19;

	private static final int CONSTANT_Package = 20;

	protected final byte[] classBytes;

	/**
	 * 转换为数据输入流
	 */
	protected DataInputStream dis;

	protected Object[] constantPool;

	protected final RASPClass raspClass = new RASPClass();

	public RASPClassReader(byte[] bytes) {
		this.dis = new DataInputStream(new ByteArrayInputStream(bytes));
		this.classBytes = bytes;
	}

	public void clear() {

	}

	/**
	 * 解析常量池数据
	 *
	 * @throws IOException 数据读取异常
	 */
	protected void parseConstantPool() throws IOException {
		// u2 constant_pool_count;
		int poolCount = dis.readUnsignedShort();
		constantPool = new Object[poolCount];

		// cp_info constant_pool[constant_pool_count-1];
		for (int i = 1; i <= poolCount - 1; i++) {
			int tag = dis.readUnsignedByte();

			switch (tag) {
				case CONSTANT_Utf8:
					constantPool[i] = dis.readUTF();
					break;
				case CONSTANT_Integer:
				case CONSTANT_Float:
					dis.readInt();
					break;
				case CONSTANT_Long:
				case CONSTANT_Double:
					dis.readLong();
					i++;
					break;
				case CONSTANT_Class:
				case CONSTANT_String:
				case CONSTANT_MethodType:
				case CONSTANT_Module:
				case CONSTANT_Package:
					constantPool[i] = dis.readUnsignedShort();
					break;
				case CONSTANT_Fieldref:
				case CONSTANT_Methodref:
				case CONSTANT_InterfaceMethodref:
				case CONSTANT_NameAndType:
				case CONSTANT_Dynamic:
				case CONSTANT_InvokeDynamic:
					dis.readUnsignedShort();
					dis.readUnsignedShort();
					break;
				case CONSTANT_MethodHandle:
					dis.readUnsignedByte();
					dis.readUnsignedShort();
					break;
				default:
					throw new IllegalArgumentException("Invalid constant pool tag: " + tag);
			}
		}

		// 解析带字符串引用的类、字符串、方法类型、模块、包
		for (int i = 0; i < constantPool.length; i++) {
			Object pool = constantPool[i];
			if (pool == null) continue;

			if (pool instanceof Integer) {
				int idx = (Integer) pool;

				constantPool[i] = constantPool[idx];
			}
		}
	}

	/**
	 * 读取异常表数据
	 *
	 * @throws IOException 读取异常
	 */
	protected void readExceptionTable() throws IOException {
		int exceptionTableLength = dis.readUnsignedShort();

		for (int i = 0; i < exceptionTableLength; i++) {
			// startPc
			dis.readUnsignedShort();

			// endPc
			dis.readUnsignedShort();

			// handlerPc
			dis.readUnsignedShort();

			// catchType
			dis.readUnsignedShort();
		}
	}

	protected void readAttribute(int attributesCount, String attributeName, RASPMember member) throws IOException {
		if ("Code".equals(attributeName)) {
			// maxStack
			dis.readUnsignedShort();

			// maxLocals
			dis.readUnsignedShort();

			int    codeLength = dis.readInt();
			byte[] bytes      = new byte[codeLength];

			// 读取所有的code字节
			dis.read(bytes);

			// 解析异常表
			readExceptionTable();

			// 解析Code的Attribute
			readAttributes(dis.readShort(), member);
		} else if ("LocalVariableTable".equals(attributeName)) {
			int localVariableTableLength = dis.readUnsignedShort();

			// 创建属性Map
			// local_variable_table[local_variable_table_length];
			for (int i = 0; i < localVariableTableLength; i++) {
				// u2 start_pc;
				dis.readUnsignedShort();

				// u2 length;
				dis.readUnsignedShort();

				// u2 name_index; 参数名称
				int nameIndex = dis.readUnsignedShort();

				String name = (String) constantPool[nameIndex];

				// u2 descriptor_index; 参数描述符
				int descriptorIndex = dis.readUnsignedShort();

				String desc = (String) constantPool[descriptorIndex];

				// u2 index;
				int index = dis.readUnsignedShort();

				// 缓存LocalVariable
				member.addAttributes("LocalVariable", new RASPLocalVariable(index, name, desc));
			}
		} else {
			dis.read(new byte[attributesCount]);
		}
	}

	protected void readAttributes(int attrCount, RASPMember member) throws IOException {
		// attribute_info attributes[attributes_count];
		for (int j = 0; j < attrCount; j++) {
			// attribute_info {
			// 	  u2 attribute_name_index;
			// 	  u4 attribute_length;
			// 	  u1 info[attribute_length];
			// }

			// u2 attribute_name_index;
			int attributeNameIndex = dis.readUnsignedShort();

			String attributeName = (String) constantPool[attributeNameIndex];

			// u2 attributes_count;
			int attributesCount = dis.readInt();

			// 解析Attribute
			readAttribute(attributesCount, attributeName, member);
		}
	}

	protected RASPMethod readMethod() throws IOException {
		return (RASPMethod) readMember(RASPMethod.class);
	}

	/**
	 * 读取成员变量或者方法的公用属性
	 *
	 * @return 成员变量或方法属性信息
	 * @throws IOException 读取异常
	 */
	protected RASPMember readMember(Class<?> type) throws IOException {
		// u2 access_flags;
		int access = dis.readUnsignedShort();

		// u2 name_index;
		String name = (String) constantPool[dis.readUnsignedShort()];

		// u2 descriptor_index;
		String desc = (String) constantPool[dis.readUnsignedShort()];

		// u2 attributes_count;
		int attributesCount = dis.readUnsignedShort();

		RASPMember member;

		if (type != null && type.isAssignableFrom(RASPMethod.class)) {
			member = new RASPMethod(access, name, desc, attributesCount);
		} else {
			member = new RASPMember(access, name, desc, attributesCount);
		}

		// 读取成员变量属性信息
		readAttributes(attributesCount, member);

		return member;
	}

	public RASPClass parseByteCode() throws IOException {
		parseBasic();
		parseMember();

		return raspClass;
	}

	protected void parseBasic() throws IOException {
		try {
			// u4 magic;
			int magic = dis.readInt();

			// 校验文件魔数
			if (0xCAFEBABE != magic) {
				throw new RuntimeException("Class文件格式错误!");
			}

			// u2 minor_version
			raspClass.setMinorVersion(dis.readUnsignedShort());

			// u2 major_version;
			raspClass.setMajorVersion(dis.readUnsignedShort());

			// 解析常量池
			parseConstantPool();

			// u2 access_flags;
			raspClass.setAccessFlags(dis.readUnsignedShort());

			// u2 this_class;
			raspClass.setThisClass((String) constantPool[dis.readUnsignedShort()]);

			// u2 super_class;
			int superClassIndex = dis.readUnsignedShort();

			// 当解析Object类的时候super_class为0
			if (superClassIndex != 0) {
				raspClass.setSuperClass((String) constantPool[superClassIndex]);
			}

			// u2 interfaces_count;
			int      interfacesCount = dis.readUnsignedShort();
			String[] interfaces      = new String[interfacesCount];

			// u2 interfaces[interfaces_count];
			for (int i = 0; i < interfacesCount; i++) {
				int index = dis.readUnsignedShort();

				// 设置接口名称
				interfaces[i] = (String) constantPool[index];
			}

			raspClass.setInterfaces(interfaces);

			clear();
		} finally {
			if (dis != null) IOUtils.closeQuietly(dis);
		}
	}

	protected void parseMember() throws IOException {
		// u2 fields_count;
		int fieldsCount = dis.readUnsignedShort();

		RASPMember[] raspFields = new RASPMember[fieldsCount];

		// field_info fields[fields_count];
		for (int i = 0; i < raspFields.length; i++) {
			raspFields[i] = readMember(null);
		}

		raspClass.setFields(raspFields);

		// u2 methods_count;
		int methodsCount = dis.readUnsignedShort();

		RASPMethod[] raspMethods = new RASPMethod[methodsCount];

		// method_info methods[methods_count];
		for (int i = 0; i < raspMethods.length; i++) {
			raspMethods[i] = readMethod();
		}

		raspClass.setMethods(raspMethods);
	}

//	public static void main(String[] args) throws IOException {
//		long             ctime = System.currentTimeMillis();
//		Collection<File> files = org.javaweb.rasp.commons.utils.FileUtils.listFiles(new File("/Users/yz/Desktop/classes"), new String[]{"class"}, true);
//
//		for (File file : files) {
//			byte[]          bytes      = FileUtils.readFileToByteArray(file);
//			RASPClassReader codeParser = new RASPClassReader(bytes);
//
//			RASPClass code = codeParser.parseByteCode();
//			System.out.println(code.getThisClass() + "," + code.getMethods().length);
//		}
//
//		System.out.println(System.currentTimeMillis() - ctime);
//	}

}
