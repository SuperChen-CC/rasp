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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.javaweb.rasp.commons.logback.classic.spi.ILoggingEvent;
import org.javaweb.rasp.commons.logback.classic.util.DefaultNestedComponentRules;
import org.javaweb.rasp.commons.logback.core.Appender;
import org.javaweb.rasp.commons.logback.core.joran.action.ActionConst;
import org.javaweb.rasp.commons.logback.core.joran.action.AppenderAction;
import org.javaweb.rasp.commons.logback.core.joran.spi.DefaultNestedComponentRegistry;
import org.javaweb.rasp.commons.logback.core.joran.spi.ElementPath;
import org.javaweb.rasp.commons.logback.core.joran.spi.ElementSelector;
import org.javaweb.rasp.commons.logback.core.joran.spi.RuleStore;
import org.javaweb.rasp.commons.logback.core.sift.SiftingJoranConfiguratorBase;

public class SiftingJoranConfigurator extends SiftingJoranConfiguratorBase<ILoggingEvent> {

    SiftingJoranConfigurator(String key, String value, Map<String, String> parentPropertyMap) {
        super(key, value, parentPropertyMap);
    }

    @Override
    protected ElementPath initialElementPath() {
        return new ElementPath("configuration");
    }

    @Override
    protected void addInstanceRules(RuleStore rs) {
        super.addInstanceRules(rs);
        rs.addRule(new ElementSelector("configuration/appender"), new AppenderAction<ILoggingEvent>());
    }

    @Override
    protected void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
        DefaultNestedComponentRules.addDefaultNestedComponentRegistryRules(registry);
    }

    @Override
    protected void buildInterpreter() {
        super.buildInterpreter();
        Map<String, Object> omap = interpreter.getInterpretationContext().getObjectMap();
        omap.put(ActionConst.APPENDER_BAG, new HashMap<String, Appender<?>>());
        //omap.put(ActionConst.FILTER_CHAIN_BAG, new HashMap());
        Map<String, String> propertiesMap = new HashMap<String, String>();
        propertiesMap.putAll(parentPropertyMap);
        propertiesMap.put(key, value);
        interpreter.setInterpretationContextPropertiesMap(propertiesMap);
    }

    @SuppressWarnings("unchecked")
    public Appender<ILoggingEvent> getAppender() {
        Map<String, Object> omap = interpreter.getInterpretationContext().getObjectMap();
        HashMap<String, Appender<?>> appenderMap = (HashMap<String, Appender<?>>) omap.get(ActionConst.APPENDER_BAG);
        oneAndOnlyOneCheck(appenderMap);
        Collection<Appender<?>> values = appenderMap.values();
        if (values.size() == 0) {
            return null;
        }
        return (Appender<ILoggingEvent>) values.iterator().next();
    }
}
