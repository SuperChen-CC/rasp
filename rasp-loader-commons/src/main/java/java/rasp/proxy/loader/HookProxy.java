package java.rasp.proxy.loader;

import static java.rasp.proxy.loader.HookResultType.RETURN;

/**
 * Creator: yz
 * Date: 2019-07-09
 */
public class HookProxy {

	public static HookResult<?> processHookEvent(
			Object thisObject, String thisClass, String thisMethodName, String thisMethodDesc,
			Object[] thisArgs, Object thisReturnValue, int thisAdviceEvent, int hookHash) throws Exception {

		// 省略RASP调度逻辑
		return new HookResult<Object>(RETURN);
	}

}
