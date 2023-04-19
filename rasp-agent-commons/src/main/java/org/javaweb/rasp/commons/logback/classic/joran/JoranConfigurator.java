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
package org.javaweb.rasp.commons.logback.classic.joran;

import org.javaweb.rasp.commons.logback.classic.joran.action.*;
import org.javaweb.rasp.commons.logback.classic.sift.SiftAction;
import org.javaweb.rasp.commons.logback.classic.spi.ILoggingEvent;
import org.javaweb.rasp.commons.logback.classic.util.DefaultNestedComponentRules;
import org.javaweb.rasp.commons.logback.core.joran.JoranConfiguratorBase;
import org.javaweb.rasp.commons.logback.core.joran.action.AppenderRefAction;
import org.javaweb.rasp.commons.logback.core.joran.action.IncludeAction;
import org.javaweb.rasp.commons.logback.core.joran.action.NOPAction;
import org.javaweb.rasp.commons.logback.core.joran.spi.DefaultNestedComponentRegistry;
import org.javaweb.rasp.commons.logback.core.joran.spi.ElementSelector;
import org.javaweb.rasp.commons.logback.core.joran.spi.RuleStore;

/**
 * JoranConfigurator class adds rules specific to logback-classic.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class JoranConfigurator extends JoranConfiguratorBase<ILoggingEvent> {

    @Override
    public void addInstanceRules(RuleStore rs) {
        // parent rules already added
        super.addInstanceRules(rs);

        rs.addRule(new ElementSelector("configuration"), new ConfigurationAction());

        rs.addRule(new ElementSelector("configuration/contextName"), new ContextNameAction());
        rs.addRule(new ElementSelector("configuration/contextListener"), new LoggerContextListenerAction());

        rs.addRule(new ElementSelector("configuration/appender/sift"), new SiftAction());
        rs.addRule(new ElementSelector("configuration/appender/sift/*"), new NOPAction());

        rs.addRule(new ElementSelector("configuration/logger"), new LoggerAction());
        rs.addRule(new ElementSelector("configuration/logger/level"), new LevelAction());

        rs.addRule(new ElementSelector("configuration/root"), new RootLoggerAction());
        rs.addRule(new ElementSelector("configuration/root/level"), new LevelAction());
        rs.addRule(new ElementSelector("configuration/logger/appender-ref"), new AppenderRefAction<ILoggingEvent>());
        rs.addRule(new ElementSelector("configuration/root/appender-ref"), new AppenderRefAction<ILoggingEvent>());

        // add if-then-else support
        rs.addRule(new ElementSelector("*/if/then/*"), new NOPAction());
        rs.addRule(new ElementSelector("*/if/else/*"), new NOPAction());

        rs.addRule(new ElementSelector("configuration/include"), new IncludeAction());

        rs.addRule(new ElementSelector("configuration/consolePlugin"), new ConsolePluginAction());

        rs.addRule(new ElementSelector("configuration/receiver"), new ReceiverAction());

    }

    @Override
    protected void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
        DefaultNestedComponentRules.addDefaultNestedComponentRegistryRules(registry);
    }

}
