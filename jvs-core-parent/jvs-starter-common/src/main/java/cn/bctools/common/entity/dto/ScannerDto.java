package cn.bctools.common.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
@ApiModel("接口信息")
public class ScannerDto {

    @ApiModelProperty("客户端id")
    String clientId;

    @ApiModelProperty("服务名")
    String applicationName;

    @ApiModelProperty("客户端密钥")
    String clientSecret;

    @ApiModelProperty("请求对象集合")
    List<RequestUrlDto> list;

    @ApiModelProperty("数据权限标识")
    List<DataDictDto> dataDictDtos;
}