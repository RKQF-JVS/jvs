package cn.bctools.message.push.dto.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class WxMpTemplateVo implements Serializable {

    private static final long serialVersionUID = -7642969617612376094L;
    /**
     * template_id.
     * 模板ID
     */
    private String templateId;
    /**
     * title.
     * 模板标题
     */
    private String title;
    /**
     * primary_industry.
     * 模板所属行业的一级行业
     */
    private String primaryIndustry;
    /**
     * deputy_industry.
     * 模板所属行业的二级行业
     */
    private String deputyIndustry;
    /**
     * content.
     * 模板内容
     */
    private String content;
    /**
     * example.
     * 模板示例
     */
    private String example;
}
