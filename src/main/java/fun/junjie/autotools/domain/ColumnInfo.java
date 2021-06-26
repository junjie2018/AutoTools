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
     * 当前列如果作为Bean的，则Bean类名称
     * （如果字段是一个对象或枚举，则该名称有意义）
     * （已转下划线为驼峰，首字母大写）
     */
    private String beanClass;

    /**
     * 当前列如果作为Bean的，则Bean对象名称
     * （已转下划线为驼峰，首字母小写）
     */
    private String beanObject;

    /**
     * 字段类型（数据库中的字段映射到Java中的类型）
     */
    private String fieldType;

    /**
     * 当前列包含的枚举信息
     */
    private EnumInfo enumInfo;

    /**
     * 当前列包含的内部类信息
     */
    private InternalClassInfo internalClassInfo;
}
