package cn.bctools.mongodb.core;


import java.lang.annotation.Annotation;
import java.util.Objects;

public class FieldAnnotationCache {
    /**
     * 连接符注解
     */
    private Class<? extends Annotation> operatorAnnotation;
    /**
     * 条件注解
     */
    private Class<? extends Annotation> conditionsAnnotation;
    /**
     * 条件注解的value 默认则为Field的Name
     * 对应mongodb字段名
     */
    private String filedName;

    /**
     * 是否标注No注解
     *
     * @param o
     * @return
     */
    private boolean no;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldAnnotationCache that = (FieldAnnotationCache) o;
        return no == that.no && Objects.equals(operatorAnnotation, that.operatorAnnotation) && Objects.equals(conditionsAnnotation, that.conditionsAnnotation) && Objects.equals(filedName, that.filedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operatorAnnotation, conditionsAnnotation, filedName, no);
    }

    public Class<? extends Annotation> getOperatorAnnotation() {
        return operatorAnnotation;
    }

    public void setOperatorAnnotation(Class<? extends Annotation> operatorAnnotation) {
        this.operatorAnnotation = operatorAnnotation;
    }

    public Class<? extends Annotation> getConditionsAnnotation() {
        return conditionsAnnotation;
    }

    public void setConditionsAnnotation(Class<? extends Annotation> conditionsAnnotation) {
        this.conditionsAnnotation = conditionsAnnotation;
    }

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public boolean isNo() {
        return no;
    }

    public void setNo(boolean no) {
        this.no = no;
    }
}
