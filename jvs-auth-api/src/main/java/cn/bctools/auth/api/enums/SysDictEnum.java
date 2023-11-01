package cn.bctools.auth.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统字典类型
 *
 * @Author: GuoZi
 */
@Getter
@AllArgsConstructor
public enum SysDictEnum {

    /**
     * 知识库模板分类
     */
    knowledge_template_type,
    /**
     * 开发套件模板分类
     */
    design_template_type,
    /**
     * 应用创建页面的链接
     */
    jvs_app_links,
    ;

}
