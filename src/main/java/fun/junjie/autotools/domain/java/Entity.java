//package fun.junjie.autotools.domain.java;
//
//
//import fun.junjie.autotools.utils.JStringUtils;
//import lombok.Data;
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Data
//public class Entity {
//    /**
//     * entityName
//     */
//    private String entityName;
//
//    /**
//     * EntityName
//     */
//    private String entityClassName;
//
//    /**
//     * fun.junjie.entity
//     */
//    private String entityPackageName;
//
//    /**
//     * fun.junjie.entity.EntityName
//     */
//    private String entityFullPath;
//
//    private List<Annotation> annotationList;
//
//    private List<Field> fieldList;
//    private List<ObjectField> objectFieldList;
//
//    public Entity(String tableName) {
//
//        // 去掉表前缀
//        if (tableName.startsWith(UserConfig.TABLE_PREFIX)) {
//            tableName = tableName.substring(UserConfig.TABLE_PREFIX.length());
//        }
//
//        // 下滑转驼峰
//        tableName = JStringUtils.underlineToCamel(tableName);
//
//        this.entityName = tableName;
//        this.entityClassName = StringUtils.capitalize(tableName) + UserConfig.ENTITY_SUFFIX;
//        this.entityPackageName = UserConfig.ENTITY_PACKAGE;
//        this.entityFullPath = this.entityPackageName + "." + this.entityClassName;
//
//        annotationList = new ArrayList<>();
//        fieldList = new ArrayList<>();
//        objectFieldList = new ArrayList<>();
//    }
//
//    public void addField(Field field) {
//        // 用户配置的忽略字段
//        if (UserConfig.FIELD_TO_IGNORE.contains(field.getFieldName())) {
//            return;
//        }
//
//        // 该部分数据用于渲染静态内部类
//        if (field.getFieldType() == JavaType.Object) {
//            objectFieldList.add(new ObjectField(field.getFieldName()));
//        }
//
//        fieldList.add(field);
//    }
//}
