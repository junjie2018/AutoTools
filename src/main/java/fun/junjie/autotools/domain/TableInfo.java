package fun.junjie.autotools.domain;

import lombok.Data;

import java.util.List;

@Data
public class TableInfo {

    /**
     * 数据库中的表名称
     */
    private String tableName;

    /**
     * 数据库中的列描述
     */
    private String tableComment;

    /**
     * 类别名（由用户指定的别名，如果未指定，其值为tableComment）
     */
    private String tableAlias;

    /**
     * 实体名称（已去除表前缀，并转下划线为驼峰）
     */
    private String entityClassName;

    /**
     * 实体名称（已去除表前缀，并转下划线为驼峰，且首字母小写）
     */
    private String entityObjectName;


    /**
     * 表中所拥有的列信息
     */
    private List<ColumnInfo> columnInfos;

    /**
     * 表中所拥有的枚举信息
     */
    private List<EnumInfo> enumInfos;

    /**
     * 表中所拥有的内部类信息
     */
    private List<InternalClassInfo> internalClassInfos;
}
