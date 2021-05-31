package fun.junjie.autotools.domain.java;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class TableInfo {

    private String tableDesc;
    private String tableNameWithPrefix;
    private String tableNameWithoutPrefix;
    private String tableJavaNameCapitalized;
    private String tableJavaNameUncapitalized;
    private String tableJavaNamePlural;

    private List<EnumClass> enums;
    private List<InnerClass> innerClasses;

    /**
     * 由用户定义的映射，并不是每张表都会有该字段，只有那种需要
     * 生成Controller、Service的表才会有该字段
     */
    private String entityName;
    private String entityPackageName;
    private Set<String> entityPackagesToImport;
    private List<Field> entityFields;
    private List<InnerClass> entityInnerClasses;

    @Data
    public static class Field {
        private String fieldNameCapitalized;
        private String fieldNameUncapitalized;
        private String fieldDesc;
        private String fieldType;
        private Set<String> annotations;
    }

    @Data
    public static class InnerClass {
        private String innerClassName;
        private String innerClassDesc;
        private List<Field> innerClassFields;
    }

    @Data
    public static class EnumClass {
        private String enumName;
        private String enumDesc;
        private List<EnumItem> enumItems;
    }

    @Data
    public static class EnumItem {
        private String enumItemName;
        private String enumItemValue;
        private String enumItemDesc;
    }
}
