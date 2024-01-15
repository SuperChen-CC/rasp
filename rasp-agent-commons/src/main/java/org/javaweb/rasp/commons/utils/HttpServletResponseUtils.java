package org.javaweb.rasp.commons.utils;

import org.javaweb.rasp.commons.attack.RASPAttackInfo;
import org.javaweb.rasp.commons.cache.RASPRequestCached;
import org.javaweb.rasp.commons.context.RASPServletRequestContext;
import org.javaweb.rasp.commons.servlet.HttpServletRequestProxy;
import org.javaweb.rasp.commons.servlet.HttpServletResponseProxy;

import java.io.OutputStream;
import java.io.Writer;
import java.rasp.proxy.loader.RASPModuleType;

import static org.javaweb.rasp.commons.log.RASPLogger.errorLog;
import static org.javaweb.rasp.commons.utils.HttpServletRequestUtils.htmlSpecialChars;
import static org.javaweb.rasp.commons.utils.StringUtils.isNotEmpty;
import static org.javaweb.rasp.loader.AgentConstants.AGENT_NAME;

public class HttpServletResponseUtils {

	public static void responseJson(RASPServletRequestContext context, String text) {
		response(context, "application/json;charset=UTF-8", text);
	}

	public static void responseJson(RASPServletRequestContext context, Object obj) {
		response(context, "application/json;charset=UTF-8", JsonUtils.toJson(obj));
	}

	public static void responseXml(RASPServletRequestContext context, String text) {
		response(context, "text/xml;charset=UTF-8", text);
	}

	public static void responseHTML(RASPServletRequestContext context, String text) {
		response(context, "text/html;charset=UTF-8", text);
	}

	public static void responseText(RASPServletRequestContext context, String text) {
		response(context, "text/plain;charset=UTF-8", text);
	}

	public static void accessDenied(RASPServletRequestContext context, RASPAttackInfo attack, String text) {
		String         attackHash = attack.getAttackHash();
		RASPModuleType moduleType = attack.getModuleType();

		if (!context.isContextClosed() && isNotEmpty(text) && moduleType != null) {
			HttpServletRequestProxy request     = context.getServletRequest();
			String                  queryString = request.getQueryString();
			StringBuffer            requestURL  = request.getRequestURL();

			String url = "";

			if (requestURL != null) {
				url = requestURL.toString();
			}

			if (queryString != null) {
				url += "?" + htmlSpecialChars(queryString);
			}

			String attackInfo = "{" +
					"'agent-name': '" + AGENT_NAME + "', 'attack-name': '" + moduleType.getModuleName() + "'," +
					"'request-url': '" + url + "', 'attack-desc': '" + moduleType.getModuleDesc() + "'," +
					"'attack-hash': '" + attackHash + "'" +
					"}";

			text = text.replace("$attackInfo", attackInfo);
		}

		response(context, "text/html;charset=UTF-8", text);
	}

	public static void response(RASPServletRequestContext context, String contentType, String text) {
		RASPRequestCached        cachedRequest = context.getCachedRequest();
		HttpServletResponseProxy response      = context.getServletResponse();
		Object                   output        = cachedRequest.getOutput();

		try {
			if (response == null || text == null) {
				return;
			}

			response.setContentType(contentType);

			if (output == null) {
				output = response.getWriter();
			}

			// JSP out
			if (output instanceof Writer) {
				Writer out = (Writer) output;
				out.write(text);
				out.flush();
//				out.close();
			} else {
				OutputStream out = (OutputStream) output;
				out.write(text.getBytes());
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			errorLog("返回信息[" + text + "]异常:", e);
		}
	}

}
