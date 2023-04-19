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
package org.javaweb.rasp.commons.logback.classic.sift;

import java.util.List;
import java.util.Map;

import org.javaweb.rasp.commons.logback.classic.spi.ILoggingEvent;
import org.javaweb.rasp.commons.logback.core.joran.event.SaxEvent;
import org.javaweb.rasp.commons.logback.core.sift.AbstractAppenderFactoryUsingJoran;
import org.javaweb.rasp.commons.logback.core.sift.SiftingJoranConfiguratorBase;

/**
 *
 */
public class AppenderFactoryUsingJoran extends AbstractAppenderFactoryUsingJoran<ILoggingEvent> {

    AppenderFactoryUsingJoran(List<SaxEvent> eventList, String key, Map<String, String> parentPropertyMap) {
        super(eventList, key, parentPropertyMap);
    }

    public SiftingJoranConfiguratorBase<ILoggingEvent> getSiftingJoranConfigurator(String discriminatingValue) {
        return new SiftingJoranConfigurator(key, discriminatingValue, parentPropertyMap);
    }

}
