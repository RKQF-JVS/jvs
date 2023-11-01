package cn.bctools.oauth2.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: GuoZi
 */
@Data
@ConfigurationProperties("jvs.oauth2")
public class JvsOAuth2Property {

    /**
     * 放行路径
     */
    private List<String> permitUrls = new ArrayList<>(16);

}
