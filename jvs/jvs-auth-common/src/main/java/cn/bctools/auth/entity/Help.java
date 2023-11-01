package cn.bctools.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@TableName(value = "sys_help")
public class Help {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("终端Id")
    private String clientId;
    @ApiModelProperty("终端名称")
    private String clientName;
    @ApiModelProperty("帮助label")
    private String label;
    @ApiModelProperty("视频还是连接，直接输入中文即可,\n" +
            "\n" +
            "设计器\n" +
            "     目录\n" +
            "         组件\n" +
            "             视频\n" +
            "             链接\n" +
            "             文本\n" +
            " \n" +
            "\n" +
            "视频\n" +
            "链接\n" +
            "文本\n" +
            " ")
    private String type;
    @ApiModelProperty("上级ID")
    private String parentId;
    @ApiModelProperty("内容")
    private String content;
    @TableField(exist = false)
    @ApiModelProperty("子集")
    private List<Help> children;
}
