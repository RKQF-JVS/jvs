package cn.bctools.auth.entity;


import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@Slf4j
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonStrTypeHandler extends AbstractJsonTypeHandler<Object> {
    @Override
    protected Object parse(String json) {
        if (JSONUtil.isJsonArray(json)) {
            return JSONObject.parseArray(json);
        } else if (JSONUtil.isJsonObj(json)) {
            return JSONObject.parseObject(json);
        }
        return json;
    }

    @Override
    protected String toJson(Object obj) {
        if (obj instanceof String) {
            return obj.toString();
        }
        return JSONObject.toJSONString(obj);
    }
}
