/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package org.javaweb.rasp.commons.logback.classic.pattern;

import org.javaweb.rasp.commons.logback.classic.spi.ILoggingEvent;
import org.javaweb.rasp.commons.logback.classic.spi.StackTraceElementProxy;
import org.javaweb.rasp.commons.logback.classic.spi.ThrowableProxyUtil;

public class ExtendedThrowableProxyConverter extends ThrowableProxyConverter {

    @Override
    protected void extraData(StringBuilder builder, StackTraceElementProxy step) {
        ThrowableProxyUtil.subjoinPackagingData(builder, step);
    }

    protected void prepareLoggingEvent(ILoggingEvent event) {

    }

}
