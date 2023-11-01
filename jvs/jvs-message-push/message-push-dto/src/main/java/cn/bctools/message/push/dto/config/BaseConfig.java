package cn.bctools.message.push.dto.config;

import com.alibaba.cloud.commons.lang.StringUtils;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;

@Data
public class BaseConfig implements Serializable {

    private static final long serialVersionUID = 7936035141727836751L;


    /**
     * 判断对象中属性值是否全为空
     *
     * @param object
     * @return
     */
    public static boolean hasNull(Object object) {
        if (null == object) {
            return true;
        }
        try {
            for (Field f : object.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.get(object) != null && StringUtils.isNotBlank(f.get(object).toString())) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
