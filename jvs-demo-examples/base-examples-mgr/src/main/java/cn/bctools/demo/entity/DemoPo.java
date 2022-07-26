package cn.bctools.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.bctools.database.annotation.JvsDataTable;
import cn.bctools.database.annotation.JvsDataTableField;
import cn.bctools.database.entity.po.BasePo;
import cn.bctools.demo.entity.enums.TypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
@TableName("demo")
@JvsDataTable(value = "demo", desc = "客户管理")
public class DemoPo extends BasePo {

    @TableId(type = IdType.ASSIGN_UUID)
    String id;
    /**
     * 类型，数据权限标记
     */
    @JvsDataTableField(name = "type", desc = "示例自定义类型")
    TypeEnum type;

}
