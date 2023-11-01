package cn.bctools.auth.entity;//package cn.bctools.auth.entity;
//
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
//import cn.bctools.gateway.entity.enums.PermissionTypeEnum;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//import lombok.experimental.Accessors;
//
//import java.io.Serializable;
//import java.util.List;
//
///**
// * 资源数据表，用于统计所有项目的请求地址
// *
// * @author
// */
//@Data
//@Accessors(chain = true)
//@TableName(value = "sys_permission", autoResultMap = true)
//public class Permission implements Serializable {
//
//    @TableId(value = "id", type = IdType.ASSIGN_UUID)
//    @ApiModelProperty(value = "主键")
//    private String id;
//    @ApiModelProperty(value = "菜单ID")
//    private String menuId;
//    /**
//     * 该值唯一, 默认使用uuid
//     */
//    @ApiModelProperty(value = "资源标识()")
//    private String permission;
//    @ApiModelProperty(value = "资源名称")
//    private String name;
//    @ApiModelProperty(value = "请求地址")
//    @TableField(typeHandler = FastjsonTypeHandler.class)
//    private List<String> api;
//    @ApiModelProperty(value = "客户端或服务端名称")
//    private String applyId;
//    @ApiModelProperty(value = "类型 button 按钮 remark 说明")
//    private PermissionTypeEnum type;
//    @ApiModelProperty(value = "解释说明")
//    private String remark;
//}
//
