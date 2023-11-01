package cn.bctools.dynamic.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.grant.Grant;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

@Getter
@AllArgsConstructor
public enum Token {
    /**
     * 查询
     **/
    SELECT(Select.class),
    /**
     * 删除
     **/
    DELETE(Delete.class),
    /**
     * 新增
     **/
    INSERT(Insert.class),
    /**
     * 更新
     **/
    UPDATE(Update.class),
    /**
     * 建表
     **/
    CREATE(CreateTable.class),
    /**
     * 变更表
     **/
    ALTER(Alter.class),
    /**
     * 删表
     **/
    DROP(Drop.class),
    /**
     * 赋权
     **/
    GRANT(Grant.class),
    ;

    private final Class<? extends Statement> type;

    public static Token getByType(Class<? extends Statement> aClass) {
        if (aClass == null) {
            return null;
        }
        for (Token token : Token.values()) {
            if (token.type.equals(aClass)) {
                return token;
            }
        }
        return null;
    }

}
