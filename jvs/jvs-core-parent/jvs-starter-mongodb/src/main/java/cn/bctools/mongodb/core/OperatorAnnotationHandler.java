package cn.bctools.mongodb.core;

import org.springframework.data.mongodb.core.query.Criteria;


@FunctionalInterface
public interface OperatorAnnotationHandler {
    /**
     * 相关运算符处理
     *
     * @param criteriaLeft
     * @param criteriaRight
     * @return
     */
    Criteria handler(Criteria criteriaLeft, Criteria criteriaRight);
}
