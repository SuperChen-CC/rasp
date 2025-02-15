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

import org.javaweb.rasp.commons.logback.classic.PatternLayout;
import org.javaweb.rasp.commons.logback.classic.spi.ILoggingEvent;
import org.javaweb.rasp.commons.logback.core.CoreConstants;

/**
 * Always returns an empty string.
 * <p>
 * This converter is useful to pretend that the converter chain for
 * PatternLayout actually handles exceptions, when in fact it does not.
 * By adding %nopex to the conversion pattern, the user can bypass
 * the automatic addition of %ex conversion pattern for patterns 
 * which do not contain a converter handling exceptions.
 * 
 * <p>Users can ignore the existence of this converter, unless they
 * want to suppress the automatic printing of exceptions by 
 * {@link PatternLayout}.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class NopThrowableInformationConverter extends ThrowableHandlingConverter {

    public String convert(ILoggingEvent event) {
        return CoreConstants.EMPTY_STRING;
    }

}
