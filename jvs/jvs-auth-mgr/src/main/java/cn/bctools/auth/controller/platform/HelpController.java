package cn.bctools.auth.controller.platform;

import cn.bctools.auth.entity.Help;
import cn.bctools.auth.mapper.HelpMapper;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Api(tags = "帮助管理")
@RestController
@AllArgsConstructor
@RequestMapping("/help")
public class HelpController {

    HelpMapper helpMapper;

    @Log(back = false)
    @GetMapping("/all")
    @ApiOperation(value = "所有数据")
    public R page(Help help) {
        if (ObjectNull.isNotNull(help.getLabel())) {
            List<Help> helps = helpMapper.selectList(new LambdaQueryWrapper<Help>().like(Help::getLabel, help.getLabel()));
            return R.ok(helps);
        }
        if (ObjectNull.isNull(help.getClientId())) {
            return R.ok();
        }
        List<Help> helps = helpMapper.selectList(Wrappers.query(help));
        if (ObjectNull.isNull(helps)) {
            return R.ok();
        }
        helps = recursionFn(helps, new Help().setId("-1"));
        return R.ok(helps);
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     * @return
     */
    private List<Help> recursionFn(List<Help> list, Help t) {
        // 得到子节点列表
        List<Help> childList = getChildList(list, t);
        t.setChildren(childList);
        for (Help tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
        return childList;
    }


    /**
     * 得到子节点列表
     */
    private List<Help> getChildList(List<Help> list, Help t) {
        List<Help> tlist = new ArrayList<Help>();
        Iterator<Help> it = list.iterator();
        while (it.hasNext()) {
            Help n = (Help) it.next();
            if (n.getParentId().equals(t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<Help> list, Help t) {
        return getChildList(list, t).size() > 0 ? true : false;
    }

    @PostMapping
    @ApiOperation(value = "新增帮助")
    public R save(@RequestBody Help help) {
        Integer integer = helpMapper.selectCount(Wrappers.query(new Help().setLabel(help.getLabel())));
        if (integer == 0) {
            helpMapper.insert(help);
            return R.ok();
        } else {
            return R.failed("标识已经存在，请修改标识");
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除帮助")
    public R delete(@PathVariable String id) {
        helpMapper.deleteById(id);
        return R.ok();
    }

    @PutMapping
    @ApiOperation(value = "修改帮助")
    public R update(@RequestBody Help help) {
        helpMapper.updateById(help);
        return R.ok();
    }

}
