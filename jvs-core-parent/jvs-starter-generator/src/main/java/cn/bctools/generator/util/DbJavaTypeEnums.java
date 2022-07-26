package cn.bctools.generator.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;

/**
 * 字段类型映射
 *
 * @Author: GuoZi
 */
@Getter
@AllArgsConstructor
public enum DbJavaTypeEnums implements Serializable {

    /**
     * db数据类型 java类型
     */
    BIT("bit", Boolean.class),
    CHAR("char", String.class),
    TINYBLOB("tinyblob", String.class),
    BLOB("blob", String.class),
    MEDIUMBLOB("mediumblob", String.class),
    LONGBLOB("longblob", String.class),
    VARCHAR("varchar", String.class),
    TINYTEXT("tinytext", String.class),
    TEXT("text", String.class),
    LONGTEXT("longtext", String.class),
    TINYINT("tinyint", Integer.class),
    SMALLINT("smallint", Integer.class),
    MEDIUMINT("mediumint", Integer.class),
    INT("int", Integer.class),
    INTEGER("integer", Integer.class),
    YEAR("year", Year.class),
    BIGINT("bigint", Long.class),
    FLOAT("float", Double.class),
    DOUBLE("double", Double.class),
    NUMBER("double", Double.class),
    DECIMAL("decimal", BigDecimal.class),
    DATE("date", LocalDate.class),
    TIME("time", LocalTime.class),
    DATETIME("datetime", LocalDateTime.class),
    TIMESTAMP("timestamp", LocalDateTime.class),
    ;

    /**
     * DB类型
     */
    private final String dbType;
    /**
     * java类型class
     */
    private final Class<?> javaType;

}
