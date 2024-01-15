package org.javaweb.rasp.commons.utils;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class RASPUtils {

	public static List<String> getNetworkMacList() {
		List<String> macList = new ArrayList<String>();

		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				byte[] macBytes = interfaces.nextElement().getHardwareAddress();

				if (macBytes != null) {
					StringBuilder macAddress = new StringBuilder();

					for (int i = 0; i < macBytes.length; i++) {
						macAddress.append(String.format("%02X%s", macBytes[i], (i < macBytes.length - 1) ? ":" : ""));
					}

					macList.add(macAddress.toString());
				}
			}
		} catch (Exception ignored) {
		}

		return macList;
	}

	public static List<String> getNetworkIPList() {
		List<String> ipList = new ArrayList<String>();

		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				Enumeration<InetAddress> addresses = interfaces.nextElement().getInetAddresses();

				while (addresses.hasMoreElements()) {
					InetAddress address = addresses.nextElement();

					// 包含IPV4和IPV6
					ipList.add(address.getHostAddress());
				}
			}
		} catch (Exception ignored) {
		}

		return ipList;
	}

	/**
	 * 获取RASP Agent 唯一标识，基于ip/mac地址 + RASP JVM参数 + 操作系统名称/类型 + 安装目录
	 *
	 * @return Agent Hashcode
	 */
	public static String getAgentHashCode(File file) {
		StringBuilder sb = new StringBuilder();

		// 合并IP、Mac列表
		List<List<String>> lists = new ArrayList<List<String>>();
		lists.add(getNetworkIPList());
		lists.add(getNetworkMacList());

		for (List<String> list : lists) {
			for (String str : list) {
				sb.append(str).append("/");
			}

			sb.append('/');
		}

		// RASP进程名称，多个进程环境hashcode一致时可以根据JVM参数区分
		sb.append(System.getProperty("rasp.name")).append('/');
		sb.append(System.getProperty("os.name")).append('/');
		sb.append(System.getProperty("os.arch")).append('/');
		sb.append(System.getProperty("user.dir")).append('/');
		sb.append(file);

		try {
			// 获取主机名
			sb.append(InetAddress.getLocalHost().getHostName()).append('/');
		} catch (UnknownHostException e) {
		}

		return EncryptUtils.md5(sb.toString());
	}

}
