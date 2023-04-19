package org.javaweb.rasp.commons;

import org.javaweb.rasp.commons.context.RASPContext;

/**
 * RASP Adapter注入成功后会主动调用这个初始化接口的init方法，该方法只会被调用一次
 */
public interface RASPAdapterInitialize {

	void init(RASPContext context, MethodHookEvent event, ClassLoader adapterClassLoader);

}
