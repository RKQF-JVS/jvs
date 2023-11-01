package cn.bctools.auth.wx;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class SysWxMpDto {

    @ApiModelProperty(value = "关注时发送的图片数据  多条时使用逗号分隔", example = "[{\"bucketName\":\"桶名称\",\"fileName\":\"文件名称\",\"fileType\":\"类型\",\"module\":\"模块\",\"originalName\":\"文件原名称\",\"size\":12},{\"bucketName\":\"桶名称\",\"fileName\":\"文件名称\",\"fileType\":\"类型\",\"module\":\"模块\",\"originalName\":\"文件原名称\",\"size\":12}]")
    private List<JSONObject> subscribeUrl;

    @ApiModelProperty("关注时的欢迎语")
    private String welcomeText;

    @ApiModelProperty("关键字信息")
    private String keywordText;

    @ApiModelProperty(value = "关键字对应的回复内容", example = "[{\"description\":\"图文消息描述\",\"title\":\"图文消息标题\",\"url\":\"点击图文消息跳转链接\",\"picUrl\":{\"bucketName\":\"桶名称\",\"fileName\":\"文件名称\",\"module\":\"模块名称\",\"originalName\":\"源文件名称\",\"size\":12,\"fileType\":\"文件类型\"},\"key\":\"关键字文本\"}]")
    private List<JSONObject> keywordJson;

}
