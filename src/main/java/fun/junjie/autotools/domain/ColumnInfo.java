package fun.junjie.autotools.domain;

import fun.junjie.autotools.domain.postgre.ColumnType;
import lombok.Data;

@Data
public class ColumnInfo {
    /**
     * 数据库中的列名称
     */
    private String columnName;

    /**
     * 数据库中的列类型
     */
    private ColumnType columnType;

    /**
     * 数据库中的列描述
     */
    private String columnComment;

    /**
     * 列别名
     */
    private String columnAlias;

    /**
     * 字段名称（已去除表前缀，并转下划线为驼峰，首字母大写）
     * （如果字段是一个对象或枚举，则该名称有意义）
     */
    private String fieldClassName;

    /**
     * 字段名称（已去除表前缀，并转下划线为驼峰，首字母小写）
     */
    private String fieldObjectName;

    /**
     * 字段类型（数据库中的字段映射到Java中的类型）
     */
    private String fieldType;

}
