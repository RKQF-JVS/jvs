package cn.bctools.feign.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SwaggerProperties
 *
 * @author My_gj
 */
@Data
@ConfigurationProperties("swagger")
public class SwaggerProperties {
    /**
     * swagger会解析的包路径
     **/
    private String basePackage = "cn.bctools";
    /**
     * 标题
     **/
    private String title = "Swagger未配置，请自行配置";
    /**
     * 详细描述
     */
    private String description = "Swagger未配置，请自行配置";

}
