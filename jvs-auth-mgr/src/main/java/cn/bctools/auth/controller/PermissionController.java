package cn.bctools.auth.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.auth.entity.Permission;
import cn.bctools.auth.service.MenuService;
import cn.bctools.auth.service.PermissionService;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author Administrator
 */
@Slf4j
@AllArgsConstructor
@Api(tags = "资源管理")
@RestController
@RequestMapping("permission")
public class PermissionController {

    PermissionService permissionService;

    MenuService menuService;

    @Log
    @GetMapping("/all/{menuId}")
    @ApiOperation(value = "所有资源", notes = "根据菜单查询所有下级的资源列表，不做分页直接做所有的数据返回")
    public R<List<Permission>> all(@PathVariable String menuId) {
        List<Permission> list = permissionService.list(Wrappers.query(new Permission().setMenuId(menuId)));
        return R.ok(list);
    }

    @Log
    @PostMapping("/permission/{menuId}")
    @ApiOperation(value = "编辑资源", notes = "根据菜单信息添加资源,可添加描述解释，也可以添加")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> permission(@RequestBody List<Permission> menu, @PathVariable String menuId) {
        // 校验资源唯一性
        Map<String, Integer> permissionMap = new HashMap<>(8);
        for (int i = 0; i < menu.size(); i++) {
            Permission permission = menu.get(i);
            String s = permission.getPermission();
            if (StringUtils.isBlank(s)) {
                permission.setPermission(UUID.randomUUID().toString());
                continue;
            }
            Integer index = permissionMap.get(s);
            if (ObjectNull.isNotNull(index)) {
                return R.failed(StrUtil.format("第{}, {}行的资源标识重复", index + 1, i + 1));
            }
            permissionMap.put(s, i);
        }
        // 先清空后添加
        permissionService.remove(Wrappers.<Permission>lambdaQuery().eq(Permission::getMenuId, menuId));
        permissionService.saveBatch(menu);
        return R.ok(true, "编辑成功");
    }

}
