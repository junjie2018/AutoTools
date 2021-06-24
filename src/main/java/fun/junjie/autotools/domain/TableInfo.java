package fun.junjie.autotools.domain;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
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
     * 业务中该实体的名称，默认情况下与tableComment相同
     */
    private String entityName;

    /**
     * 如果当前表作为Bean，则Bean类名称
     * （已去除表前缀，并转下划线为驼峰，且首字母大写）
     */
    private String beanClass;

    /**
     * 如果当前表作为Bean，则Bean类名称
     * （已去除表前缀，并转下划线为驼峰，且首字母小写）
     */
    private String beanObject;

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
