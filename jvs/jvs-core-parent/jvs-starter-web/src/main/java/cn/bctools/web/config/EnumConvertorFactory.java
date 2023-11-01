package cn.bctools.web.config;

import com.baomidou.mybatisplus.annotation.IEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * @Author: ZhuXiaoKang
 * @Description: 自定义枚举类型转换器
 */
public class EnumConvertorFactory implements ConverterFactory<String, IEnum> {

    @Override
    public <T extends IEnum> Converter<String, T> getConverter(Class<T> targetType) {
        return source -> {
            for (T t : targetType.getEnumConstants()) {
                if (String.valueOf(t.getValue()).equals(source)) {
                    return t;
                }
            }
            return null;
        };
    }
}
