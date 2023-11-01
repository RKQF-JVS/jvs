package cn.bctools.auth.entity;//package cn.bctools.auth.entity;
//
//import cn.bctools.database.entity.po.BasalPo;
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//import lombok.experimental.Accessors;
//
//import java.util.List;
//
///**
// * @author : GaoZeXi
// */
//@Data
//@ApiModel("微信公众号配置")
//@Accessors(chain = true)
//@TableName(value = "sys_wx_mp", autoResultMap = true)
//
//public class SysWxMpSettings extends BasalPo {
//
//    private static final long serialVersionUID = 1L;
//
//    @TableId(type = IdType.ASSIGN_UUID)
//    @ApiModelProperty("id")
//    private String id;
//
//    @ApiModelProperty(value = "关注时发送的图片数据  多条时使用逗号分隔", example = "[{\"bucketName\":\"桶名称\",\"fileName\":\"文件名称\",\"fileType\":\"类型\",\"module\":\"模块\",\"originalName\":\"文件原名称\",\"size\":12},{\"bucketName\":\"桶名称\",\"fileName\":\"文件名称\",\"fileType\":\"类型\",\"module\":\"模块\",\"originalName\":\"文件原名称\",\"size\":12}]")
//    @TableField(typeHandler = FastjsonTypeHandler.class)
//    private List<JSONObject> subscribeUrl;
//
//    @ApiModelProperty("关注时的欢迎语")
//    private String welcomeText;
//
//    @ApiModelProperty("关键字信息")
//    private String keywordText;
//
//    @ApiModelProperty(value = "关键字对应的回复内容", example = "[{\"description\":\"图文消息描述\",\"title\":\"图文消息标题\",\"url\":\"点击图文消息跳转链接\",\"picUrl\":{\"bucketName\":\"桶名称\",\"fileName\":\"文件名称\",\"module\":\"模块名称\",\"originalName\":\"源文件名称\",\"size\":12,\"fileType\":\"文件类型\"},\"key\":\"关键字文本\"}]")
//    @TableField(typeHandler = FastjsonTypeHandler.class)
//    private List<JSONObject> keywordJson;
//
//
//}
