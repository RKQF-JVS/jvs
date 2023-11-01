package cn.bctools.auth.template.user;

import cn.bctools.auth.entity.enums.SexTypeEnum;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: ZhuXiaoKang
 * @Description: 导入用户excel模板
 */

@Getter
@Setter
@EqualsAndHashCode
public class UserExcelTemplate {

    @ExcelProperty(value = "姓名")
    private String realName;
    @ExcelProperty(value = "性别")
    private String gender;
    @ExcelProperty(value = "手机号")
    private String phone;
    @ExcelProperty(value = "邮箱")
    private String email;
    @ExcelProperty(value = "部门名称")
    String deptName;
    @ExcelProperty(value = "岗位名称")
    String jobName;
    @ExcelProperty(value = "帐号等级")
    String level;
    @ExcelProperty(value = "职工编号")
    String employeeNo;

    @ExcelIgnore
    String deptId;
    @ExcelIgnore
    String jobId;
    @ExcelIgnore
    SexTypeEnum sex;



}
