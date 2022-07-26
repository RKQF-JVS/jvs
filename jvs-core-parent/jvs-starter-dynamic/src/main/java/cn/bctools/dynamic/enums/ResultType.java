package cn.bctools.dynamic.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Administrator
 */
@Getter
@AllArgsConstructor
public enum ResultType {

    /**
     * 对象
     */
    object(new Token[]{Token.INSERT, Token.SELECT, Token.UPDATE, Token.DELETE}, "<? extends Object>"),
    /**
     * 单行多列
     */
    map(new Token[]{Token.SELECT}, "Map<String,Object>"),
    /**
     * 多行多列
     */
    listMap(new Token[]{Token.SELECT}, "List<Map<String,Object>>"),
    /**
     * 单行单列, 布尔值
     */
    bool(new Token[]{Token.INSERT, Token.SELECT, Token.UPDATE, Token.DELETE}, "Boolean"),
    /**
     * 多行单列, 字符串
     */
    listStr(new Token[]{Token.SELECT}, "List<String>"),
    /**
     * 多行单列, 数字
     */
    listNum(new Token[]{Token.SELECT}, "List<? extends Number>"),
    /**
     * 单行单列, 字符串
     */
    str(new Token[]{Token.SELECT}, "String"),
    /**
     * 单行单列, 字符串
     */
    num(new Token[]{Token.SELECT}, "<? extends Number>"),
    page(new Token[]{Token.SELECT}, "Page<Map<String,Object>>"),
    pkValue(new Token[]{Token.INSERT}, "Serializable"),
    ;

    private final Token[] supportTokens;
    private final String returnTypeDesc;
}
