package java.rasp.proxy.loader;

/**
 * Hook点处理结果
 * Creator: yz
 * Date: 2019-06-24
 */
public class HookResultType {

	/**
	 * 直接返回什么都不做
	 */
	public static final int RETURN = 0;

	/**
	 * 抛出异常
	 */
	public static final int THROW = 1;

	/**
	 * 阻断或替换值
	 */
	public static final int REPLACE_OR_BLOCK = 2;

}
