package cn.bctools.gateway.utils;

import cn.bctools.common.exception.BusinessException;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * xss拦截工具类
 *
 * @author lieber
 */
public enum XssUtil {

    /**
     * 实例
     */
    INSTANCE;

    private final static String RICH_TEXT = "</";

    /**
     * 自定义白名单
     */
    private final static Safelist CUSTOM_WHITELIST = Safelist.relaxed()
            .addTags("a", "b", "blockquote", "br", "caption", "cite", "code", "col", "colgroup", "dd", "div", "span", "embed", "object", "dl", "dt",
                    "em", "h1", "h2", "h3", "h4", "h5", "h6", "i", "img", "li", "ol", "p", "pre", "q", "small",
                    "strike", "strong", "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", "ul")
            .addAttributes("a", "href", "title", "target")
            .addAttributes("blockquote", "cite")
            .addAttributes("col", "span")
            .addAttributes("colgroup", "span")
            .addAttributes("img", "align", "alt", "src", "title")
            .addAttributes("ol", "start")
            .addAttributes("q", "cite")
            .addAttributes("table", "summary")
            .addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width")
            .addAttributes("th", "abbr", "axis", "colspan", "rowspan", "scope", "width")
            .addAttributes("video", "src", "autoplay", "controls", "loop", "muted", "poster", "preload")
            .addAttributes("object", "width", "height", "classid", "codebase")
            .addAttributes("param", "name", "value")
            .addAttributes("embed", "src", "quality", "width", "height", "allowFullScreen", "allowScriptAccess", "flashvars", "name", "type", "pluginspage")
            .addAttributes(":all", "class", "style", "height", "width", "type", "id", "name")
            .addProtocols("blockquote", "cite", "http", "https")
            .addProtocols("cite", "cite", "http", "https")
            .addProtocols("q", "cite", "http", "https");

    /**
     * jsoup不格式化代码
     */
    private final static Document.OutputSettings OUTPUT_SETTINGS = new Document.OutputSettings().prettyPrint(false);

    /**
     * 清除json对象中的xss攻击字符
     *
     * @param val json对象字符串
     * @return 清除后的json对象字符串
     */
    private String cleanObj(String val) {
        JSONObject jsonObject = JSONObject.parseObject(val);
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            if (entry.getValue() != null && entry.getValue() instanceof String) {
                String str = (String) entry.getValue();
                str = this.cleanXss(str);
                entry.setValue(str);
            }
        }
        return jsonObject.toJSONString();
    }

    /**
     * 清除json数组中的xss攻击字符
     *
     * @param val json数组字符串
     * @return 清除后的json数组字符串
     */
    private String cleanArr(String val) {
        List<Object> list = JSONObject.parseArray(val);
        List<Object> result = new ArrayList<>(list.size());
        for (Object str : list) {
            if (JsonUtil.INSTANCE.isJsonObj(str)) {
                //对象
                str = JSONObject.parse(this.cleanXss(str));
            } else if (JsonUtil.INSTANCE.isJsonArr(str.toString())) {
                //数组
                str = JSONObject.parseArray(this.cleanXss(str));
            } else {
                //字符
                str = (this.cleanXss(str));
            }
            result.add(str);
        }
        return JSONObject.toJSONString(result);
    }

    /**
     * 清除xss攻击字符串，此处优化空间较大
     *
     * @param obj 字符串
     * @return 清除后无害的字符串
     */
    public String cleanXss(Object obj) {
        String str = obj.toString();
        if (JsonUtil.INSTANCE.isJsonObj(str)) {
            str = this.cleanObj(str);
        } else if (JsonUtil.INSTANCE.isJsonArr(str)) {
            str = this.cleanArr(str);
        } else {
            boolean richText = this.richText(str);
            if (!richText) {
                str = str.trim();
                str = str.replaceAll(" +", " ");
            }
            String afterClean = Jsoup.clean(str, "", CUSTOM_WHITELIST, OUTPUT_SETTINGS);
            if (paramError(richText, afterClean, str)) {
                throw new BusinessException("参数包含特殊字符");
            }
            str = richText ? afterClean : this.backSpecialStr(afterClean);
        }
        return str;
    }

    /**
     * 判断是否是富文本
     *
     * @param str 待判断字符串
     * @return true/false
     */
    private boolean richText(String str) {
        return str.contains(RICH_TEXT);
    }

    /**
     * 判断是否参数错误
     *
     * @param richText   是否富文本
     * @param afterClean 清理后字符
     * @param str        原字符串
     * @return true/false
     */
    private boolean paramError(boolean richText, String afterClean, String str) {
        // 如果包含富文本字符，那么不是参数错误
        if (richText) {
            return false;
        }
        // 如果清理后的字符和清理前的字符匹配，那么不是参数错误
        if (Objects.equals(str, afterClean)) {
            return false;
        }
        // 如果仅仅包含可以通过的特殊字符，那么不是参数错误
        if (Objects.equals(str, this.backSpecialStr(afterClean))) {
            return false;
        }
        // 如果还有......
        return true;
    }

    /**
     * 转义回特殊字符
     *
     * @param str 已经通过转义字符
     * @return 转义后特殊字符
     */
    private String backSpecialStr(String str) {
        return str.replaceAll("&amp;", "&");
    }
}
