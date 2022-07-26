package cn.bctools.common.utils;

/**
 * 此工具默认只使用到
 *
 * @author guojing
 */
public class StackTraceElementUtils {

    /**
     * 此工具可直接获取堆栈方法调用信息
     *
     * @param throwable 堆栈对象
     * @return
     */
    public static String logThrowableToString(Throwable throwable) {
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        StringBuffer stringBuffer = new StringBuffer(200);
        for (StackTraceElement stackTraceElement : stackTrace) {
            stringBuffer.append(stackTraceElement + "\n");
        }
        String message = throwable.getClass().getName() + ":" + throwable.getMessage() + "\n\t" + stringBuffer.toString();
        return message;
    }
}
