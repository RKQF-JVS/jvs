package cn.bctools.generator.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 表字段类型
 */
@Slf4j
public enum DbColumnType {

    // 基本类型
    BASE_BYTE("byte", null),
    BASE_SHORT("short", Integer.class),
    BASE_CHAR("char", String.class),
    BASE_INT("int", Integer.class),
    BASE_LONG("long", Integer.class),
    BASE_FLOAT("float", null),
    BASE_DOUBLE("double", null),
    BASE_BOOLEAN("boolean", Boolean.class),

    // 包装类型
    BYTE("Byte", null),
    SHORT("Short", null),
    CHARACTER("Character", null),
    INTEGER("Integer", Integer.class),
    LONG("Long", Integer.class),
    FLOAT("Float", null),
    DOUBLE("Double", null),
    BOOLEAN("Boolean", Boolean.class),
    STRING("String", String.class),

    // sql 包下数据类型
    DATE_SQL("Date", Date.class),
    TIME("Time", Time.class),
    TIMESTAMP("Timestamp", Timestamp.class),
    BLOB("Blob", String.class),
    CLOB("Clob", String.class),

    // java8 新时间类型
    LOCAL_DATE("LocalDate", LocalDate.class),
    LOCAL_TIME("LocalTime", LocalDateTime.class),
    YEAR("Year", LocalDate.class),
    YEAR_MONTH("YearMonth", LocalDate.class),
    LOCAL_DATE_TIME("LocalDateTime", LocalDateTime.class),
    INSTANT("Instant", LocalDateTime.class),

    // 其他杂类
    BYTE_ARRAY("byte[]", String.class),
    VARCHAR("varchar", String.class),
    OBJECT("Object", Map.class),
    DATE("Date", LocalDate.class),
    ENUM("enum", Enum.class),
    TINYINT("tinyint", Boolean.class),
    TEXT("text", String.class),
    BIGINT("bigint", Integer.class),
    MEDIUMINT("mediumint", String.class),
    SMALLINT("smallint", String.class),
    BIG_INTEGER("BigInteger", Integer.class);

    /**
     * 类型
     */
    private final String type;

    /**
     * 包路径
     */
    private final Class cls;

    public static Class index(String type) {
        for (DbColumnType value : DbColumnType.values()) {
            if (value.type.equals(type)) {
                return value.cls;
            }
        }
        log.error("类型不匹配 " + type);
        return null;
    }

    DbColumnType(final String type, final Class pkg) {
        this.type = type;
        this.cls = pkg;
    }

    public String getType() {
        return type;
    }

    public Class getCls() {
        return cls;
    }
}