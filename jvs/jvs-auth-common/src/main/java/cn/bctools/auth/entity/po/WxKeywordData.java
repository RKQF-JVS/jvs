package cn.bctools.auth.entity.po;

import cn.bctools.oss.dto.FileNameDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel("微信公众号配置")
@Accessors(chain = true)
public class WxKeywordData implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 关键字
     */
    private String key;
    /**
     * 图文消息标题.
     */
    private String title;

    /**
     * 图文消息描述.
     */
    private String description;

    /**
     * 图片链接.
     * 支持JPG、PNG格式，较好的效果为大图360*200，小图200*200
     */
    private FileNameDto picUrl;

    /**
     * 点击图文消息跳转链接.
     */
    private String url;
}
