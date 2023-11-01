package cn.bctools.common.utils.sensitive;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 敏感信息脱敏处理,将处理的数据转换器，
 * 打印日志中如果存在有某个字段名，则会自动脱敏处理掉
 *
 * @author AAA
 */
@Slf4j
@Component
public class SensitiveDataConverter extends MessageConverter {

    static SensitiveDataConverter sc = new SensitiveDataConverter();

    /**
     * 直接处理数据
     *
     * @param msg
     * @return
     */
    public static String processor(Object msg) {
        return sc.invokeMsg(JSONObject.toJSONString(msg));
    }

    @Override
    public String convert(ILoggingEvent event) {
        // 获取原始日志
        String oriLogMsg = event.getFormattedMessage();
        // 获取脱敏后的日志
        String afterLogMsg = invokeMsg(oriLogMsg);
        return afterLogMsg;
    }


    /**
     * 处理日志字符串，返回脱敏后的字符串
     *
     * @param oriMsg
     * @return
     */
    public String invokeMsg(final String oriMsg) {
        String tempMsg = oriMsg;
        // 处理字符串
        if (SensitiveInfoUtils.getSensitiveKey().size() > 0) {
            Set<String> keysArray = SensitiveInfoUtils.getSensitiveKey().keySet();
            for (String key : keysArray) {
                int index = -1;
                do {
                    index = tempMsg.indexOf(key, index + 1);
                    if (index != -1) {
                        // 判断key是否为单词字符
                        if (isWordChar(tempMsg, key, index)) {
                            continue;
                        }
                        // 寻找值的开始位置
                        int valueStart = getValueStartIndex(tempMsg, index + key.length());

                        // 查找值的结束位置（逗号，分号）........................
                        int valueEnd = getValueEndIndex(tempMsg, valueStart);

                        // 对获取的值进行脱敏
                        String subStr = tempMsg.substring(valueStart, valueEnd);
                        //调用脱敏处理
                        subStr = SensitiveInfoUtils.processor(key, subStr);
                        ///////////////////////////
                        tempMsg = tempMsg.substring(0, valueStart) + subStr + tempMsg.substring(valueEnd);
                    }
                } while (index != -1);
            }
        }
        return tempMsg;
    }


    private static Pattern pattern = Pattern.compile("[0-9a-zA-Z]");

    /**
     * 判断从字符串msg获取的key值是否为单词 ， index为key在msg中的索引值
     *
     * @return
     */
    private boolean isWordChar(String msg, String key, int index) {

        // 必须确定key是一个单词............................
        if (index != 0) {
            // 判断key前面一个字符
            char preCh = msg.charAt(index - 1);
            Matcher match = pattern.matcher(preCh + "");
            if (match.matches()) {
                return true;
            }
        }
        // 判断key后面一个字符
        char nextCh = msg.charAt(index + key.length());
        Matcher match = pattern.matcher(nextCh + "");
        if (match.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 获取value值的开始位置
     *
     * @param msg        要查找的字符串
     * @param valueStart 查找的开始位置
     * @return
     */
    private int getValueStartIndex(String msg, int valueStart) {
        // 寻找值的开始位置.................................
        do {
            char ch = msg.charAt(valueStart);
            // key与 value的分隔符
            if (ch == ':' || ch == '=') {
                valueStart++;
                ch = msg.charAt(valueStart);
                if (ch == '"') {
                    valueStart++;
                }
                break;
                // 找到值的开始位置
            } else {
                valueStart++;
            }
        } while (true);
        return valueStart;
    }

    /**
     * 获取value值的结束位置
     *
     * @return
     */
    private int getValueEndIndex(String msg, int valueEnd) {
        do {
            if (valueEnd == msg.length()) {
                break;
            }
            char ch = msg.charAt(valueEnd);
            // 引号时，判断下一个值是结束，分号还是逗号决定是否为值的结束
            if (ch == '"') {
                if (valueEnd + 1 == msg.length()) {
                    break;
                }
                char nextCh = msg.charAt(valueEnd + 1);
                if (nextCh == ';' || nextCh == ',') {
                    // 去掉前面的 \  处理这种形式的数据
                    while (valueEnd > 0) {
                        char preCh = msg.charAt(valueEnd - 1);
                        if (preCh != '\\') {
                            break;
                        }
                        valueEnd--;
                    }
                    break;
                } else {
                    valueEnd++;
                }
            } else if (ch == ';' || ch == ',' || ch == '}') {
                break;
            } else {
                valueEnd++;
            }

        } while (true);
        return valueEnd;
    }
}
