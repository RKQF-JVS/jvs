package cn.bctools.auth.controller;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import cn.bctools.auth.entity.SysDictItem;
import cn.bctools.auth.service.SysDictService;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import cn.bctools.redis.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Slf4j
@AllArgsConstructor
@Api(tags = "图标")
@RestController
@RequestMapping("/icon")
public class IconController {

    RedisUtils redisUtils;
    SysDictService sysDictService;

    private static final String REDIS_KEY_ICONS = "icons";
    private static final String ITEM_TYPE_ICON = "icon";

    @Log(back = false)
    @GetMapping("/all")
    @ApiOperation(value = "所有图标", notes = "根据类型返回所有图标")
    public R all() {
        List<JSONObject> list;
        Boolean exist = redisUtils.hasKey(REDIS_KEY_ICONS);
        if (exist) {
            Object icons = redisUtils.get(REDIS_KEY_ICONS);
            return R.ok(icons);
        }
        List<SysDictItem> icon = sysDictService.getDictByType(ITEM_TYPE_ICON);
        list = icon.stream().map(e -> {
            String s = HttpUtil.get(e.getValue());
            List<String> collect = Arrays.stream(s.split("\n")).filter(v -> v.startsWith(".icon-") && v.contains(":"))
                    .map(v -> v.substring(1, v.indexOf(":")))
                    .collect(Collectors.toList());
            String s1 = JSONObject.toJSONString(e);
            JSONObject o = JSONObject.parseObject(s1);
            o.put("list", collect);
            return o;
        }).collect(Collectors.toList());

        redisUtils.set(REDIS_KEY_ICONS, list);
        return R.ok(list);
    }

}
