package org.javaweb.rasp.commons.context;

import org.javaweb.rasp.commons.MethodHookEvent;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import static org.javaweb.rasp.commons.config.RASPWhitelist.isWhitelistRequest;

public abstract class RASPDefaultContext extends RASPContext {

	/**
	 * 客户端IP
	 */
	private final String requestIP;

	/**
	 * 服务端IP
	 */
	private final String serverIP;

	/**
	 * 服务端口
	 */
	private final int serverPort;

	/**
	 * 请求的URL地址
	 */
	protected String requestPath;

	/**
	 * 是否是白名单
	 */
	protected boolean whitelist = true;

	public RASPDefaultContext(MethodHookEvent event, String requestIP, String serverIP, int serverPort) {
		super(event);

		this.requestIP = requestIP;
		this.serverIP = serverIP;
		this.serverPort = serverPort;
	}

	public RASPDefaultContext(MethodHookEvent event, Socket socket) {
		super(event);

		InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();

		this.requestIP = isa.getAddress().getHostAddress();
		this.serverIP = socket.getLocalAddress().getHostAddress();
		this.serverPort = socket.getPort();
	}

	public RASPDefaultContext(MethodHookEvent event, SocketChannel sc) {
		this(event, sc.socket());
	}

	@Override
	public String getRequestIP() {
		return this.requestIP;
	}

	@Override
	public String getServerIP() {
		return serverIP;
	}

	@Override
	public int getServerPort() {
		return serverPort;
	}

	public void initRequestPath(String requestPath) {
		this.requestPath = requestPath;
		this.whitelist = isWhitelistRequest(this);
	}

	@Override
	public String getRequestPath() {
		return requestPath;
	}

	@Override
	public boolean isWhitelist() {
		return whitelist;
	}

}
