package cn.bctools.common.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

/**
 * 请求URL信息
 *
 * @Author: GuoZi
 */
@Data
@Accessors(chain = true)
@ApiModel("请求对象信息")
public class RequestUrlDto implements Serializable {

    @ApiModelProperty("请求地址")
    String api;

    @ApiModelProperty("接口请求方法")
    RequestMethod method;

}
