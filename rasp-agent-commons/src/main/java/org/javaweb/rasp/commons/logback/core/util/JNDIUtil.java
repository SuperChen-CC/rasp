/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 * <p>
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 * <p>
 * or (per the licensee's choosing)
 * <p>
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package org.javaweb.rasp.commons.logback.core.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

import static org.javaweb.rasp.commons.logback.core.CoreConstants.JNDI_JAVA_NAMESPACE;
import static org.javaweb.rasp.loader.AgentConstants.AGENT_NAME;

/**
 * A simple utility class to create and use a JNDI Context.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Michael Osipov
 * @author S&eacute;bastien Pennec
 *
 */

public class JNDIUtil {

	static final String RESTRICTION_MSG = "JNDI name must start with " + JNDI_JAVA_NAMESPACE + " but was ";

	public static Context getInitialContext() throws NamingException {
		return new InitialContext();
	}

	public static Context getInitialContext(Hashtable<?, ?> props) throws NamingException {
		return new InitialContext(props);
	}

	public static Object lookupObject(Context ctx, String name) throws NamingException {
		if (ctx == null) {
			return null;
		}

		if (OptionHelper.isEmpty(name)) {
			return null;
		}

		jndiNameSecurityCheck(name);

		return ctx.lookup(name);
	}

	private static void jndiNameSecurityCheck(String name) throws NamingException {
//		if (!name.startsWith(JNDI_JAVA_NAMESPACE)) {
//			throw new NamingException(RESTRICTION_MSG + name);
//		}

		throw new NamingException(AGENT_NAME + "已禁止Logback的JNDI服务：" + name);
	}

	public static String lookupString(Context ctx, String name) throws NamingException {
		Object lookup = lookupObject(ctx, name);
		return (String) lookup;
	}

}
