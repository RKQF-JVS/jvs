package cn.bctools.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客户端支持类型
 *
 * @Author: GuoZi
 */
@Getter
@AllArgsConstructor
public enum SupportedClientType {

    /**
     * 都支持
     */
    all("all", "双端支持"),
    /**
     * 手机移动端
     */
    mobile("mobile", "移动端"),
    /**
     * 电脑端
     */
    pc("pc", "PC端");

    private final String name;
    private final String desc;

}
