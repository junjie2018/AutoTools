package fun.junjie.autotools.domain;


import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
public class UserConfig {
    /**
     * 表前缀
     */
    public final static String TABLE_PREFIX;

    /**
     * 生成实体时忽略表中的哪些字段
     */
    public final static HashSet<String> FIELD_TO_IGNORE;


    // region entity配置

    /**
     * 实体输出时的包名
     */
    public final static String ENTITY_PACKAGE;

    /**
     * 实体的后缀
     */
    public final static String ENTITY_SUFFIX;


    /**
     * 目标项目的根路径
     */
    public final static String ENTITY_PROJECT_ROOT_PATH;


    /**
     * 目标项目依赖的jar位置
     */
    public final static String ENTITY_PROJECT_DEPENDENCY_PATH;


    /**
     * 目标项目依赖的classes文件位置
     */
    public final static String ENTITY_PROJECT_CLASSES_PATH;


    /**
     * 目标项目源码位置
     */
    public final static String ENTITY_PROJECT_JAVA_PATH;

    /**
     * 目标项目entity包所在位置
     */
    public final static String ENTITY_PROJECT_ENTITY_PACKAGE_PATH;

    // endregion

    // region enum配置

    /**
     * 枚举输出时的包名
     */
    public final static String ENUM_PACKAGE;

    /**
     * 目标项目的根路径
     */
    public final static String ENUM_PROJECT_ROOT_PATH;


    /**
     * 目标项目依赖的jar位置
     */
    public final static String ENUM_PROJECT_DEPENDENCY_PATH;


    /**
     * 目标项目源码位置
     */
    public final static String ENUM_PROJECT_JAVA_PATH;

    /**
     * 目标项目entity包所在位置
     */
    public final static String ENUM_PROJECT_ENTITY_PACKAGE_PATH;

    /**
     * 需要屏蔽的表
     */
    public final static Set<String> TABLES_TO_IGNORE;

    /**
     * 需要屏蔽的列
     */
    public final static Set<String> COLUMN_TO_IGNORE;

    // endregion

    static {

        // 表
        TABLE_PREFIX = "t_dyf_";
        FIELD_TO_IGNORE = new HashSet<>(Arrays.asList("id", "creator", "modifier", "gmtCreateTime", "gmtModifyTime"));

        // entity

        ENTITY_PACKAGE = "com.sdstc.dyf.admin.core.po";
        ENTITY_SUFFIX = "Po";
        ENTITY_PROJECT_ROOT_PATH = "D:\\Project\\dyf\\dyf-admin\\dyf-admin-core";
        ENTITY_PROJECT_DEPENDENCY_PATH = ENTITY_PROJECT_ROOT_PATH + "\\target\\dependency";
        ENTITY_PROJECT_CLASSES_PATH = ENTITY_PROJECT_ROOT_PATH + "\\target\\classes";
        ENTITY_PROJECT_JAVA_PATH = ENTITY_PROJECT_ROOT_PATH + "\\src\\main\\java";
        ENTITY_PROJECT_ENTITY_PACKAGE_PATH = ENTITY_PROJECT_JAVA_PATH + "\\" + StringUtils.join(ENTITY_PACKAGE.split("\\."), "\\");

        // enum

        ENUM_PACKAGE = "com.sdstc.dyf.admin.core.constant";
        ENUM_PROJECT_ROOT_PATH = "D:\\Project\\dyf\\dyf-admin\\dyf-admin-core";
        ENUM_PROJECT_DEPENDENCY_PATH = ENUM_PROJECT_ROOT_PATH + "\\target\\dependency";
        ENUM_PROJECT_JAVA_PATH = ENUM_PROJECT_ROOT_PATH + "\\src\\main\\java";
        ENUM_PROJECT_ENTITY_PACKAGE_PATH = ENUM_PROJECT_JAVA_PATH + StringUtils.join(ENUM_PACKAGE.split("\\."), "\\");

        TABLES_TO_IGNORE = new HashSet<>(Arrays.asList("t_dyf_data"));
        COLUMN_TO_IGNORE = new HashSet<>(Arrays.asList(
                "id",
                "creator",
                "modifier",
                "gmt_create_time",
                "gmt_modify_time",
                "is_delete"
        ));
    }

}
