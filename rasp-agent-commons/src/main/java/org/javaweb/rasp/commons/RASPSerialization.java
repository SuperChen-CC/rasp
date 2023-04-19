package org.javaweb.rasp.commons;

import org.javaweb.rasp.commons.cache.RASPByteArrayInputStream;
import org.javaweb.rasp.commons.context.RASPContext;

import java.rasp.proxy.loader.HookResult;

public interface RASPSerialization {

	HookResult<?> deserialization(RASPContext context, RASPByteArrayInputStream in);

}
