//package fun.junjie.autotools.service;
//
//import fun.junjie.autotools.config.ProjectConfig;
//import fun.junjie.autotools.config.tools.ToolsConfig;
//import fun.junjie.autotools.domain.java.TableInfo;
//import fun.junjie.autotools.domain.postgre.Table;
//import fun.junjie.autotools.domain.yaml.*;
//import fun.junjie.autotools.domain.yaml.JavaType;
//import fun.junjie.autotools.utils.JStringUtils;
//import fun.junjie.autotools.utils.TemplateUtils;
//import fun.junjie.autotools.utils.YamlUtils;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Service;
//
//import java.nio.file.Paths;
//import java.util.*;
//
//@SuppressWarnings({"Duplicates", "AlibabaMethodTooLong"})
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class PGService {
//
//
//    private final TableService2 tableService;
//    private final ProjectConfig projectConfig;
//    private final ToolsConfig toolsConfig;
//
//    private List<TableInfo> getTableInfos(List<TableRoot> tableRoots) {
//
//        List<TableInfo> result = new ArrayList<>();
//
//        for (TableRoot tableRoot : tableRoots) {
//            TableInfo tableInfo = new TableInfo();
//
//            tableInfo.setTableDesc(tableRoot.getTblDesc());
//            tableInfo.setTableNameWithPrefix(tableRoot.getTblName());
//            tableInfo.setTableNameWithoutPrefix(JStringUtils.removeTableNamePrefix(tableRoot.getTblName()));
//            tableInfo.setTableJavaNameCapitalized(JStringUtils.underlineToCamelCapitalized(JStringUtils.removeTableNamePrefix(tableRoot.getTblName())));
//            tableInfo.setTableJavaNameUncapitalized(JStringUtils.underlineToCamelUncapitalized(JStringUtils.removeTableNamePrefix(tableRoot.getTblName())));
////            tableInfo.setEntityName(toolsConfig.getEntityName(tableRoot.getTblName()));
//
//            List<TableInfo.EnumClass> enumClasses = new ArrayList<>();
//            List<TableInfo.InnerClass> innerClasses = new ArrayList<>();
//
//            List<TableInfo.Field> fields = new ArrayList<>();
//
//            for (TableRoot.ColumnRoot column : tableRoot.getColumns()) {
//                TableInfo.Field field = new TableInfo.Field();
//
//                field.setFieldName(column.getColName());
//                field.setFieldNameCapitalized(JStringUtils.underlineToCamelCapitalized(column.getColName()));
//                field.setFieldNameUncapitalized(JStringUtils.underlineToCamelUncapitalized(column.getColName()));
//                field.setFieldDesc(column.getColDesc());
//                field.setIsPrimaryKey(toolsConfig.isPrimaryKey(tableInfo.getTableNameWithPrefix(), column.getColName()));
//                if (column.getJavaType() == JavaType.STRING_ENUM || column.getJavaType() == JavaType.NUMBER_ENUM) {
//                    field.setIsEnumType(true);
//                    field.setEnumValueType(column.getJavaType() == JavaType.STRING_ENUM ? "String" : "Integer");
//                } else {
//                    field.setIsEnumType(false);
//                }
//
//                switch (column.getJavaType()) {
//                    case DATE:
//                        field.setFieldType("LocalDateTime");
//                        break;
//                    case NUMBER:
//                        field.setFieldType("Long");
//                        break;
//                    case STRING:
//                        field.setFieldType("String");
//                        break;
//                    case STRING_ENUM:
//                    case NUMBER_ENUM:
//                    case OBJECT:
//                        break;
//                    default:
//                        throw new RuntimeException("Unknown JavaType");
//                }
//
//                if (column.getJavaType() == JavaType.STRING_ENUM || column.getJavaType() == JavaType.NUMBER_ENUM) {
//                    field.setFieldType(JStringUtils.underlineToCamelCapitalized(column.getEnums().getEnumName()));
//
//                    List<TableInfo.EnumItem> enumItems = new ArrayList<>();
//                    for (TableRoot.EnumItem enumItem : column.getEnums().getEnumItems()) {
//                        TableInfo.EnumItem enumItemToAdd = new TableInfo.EnumItem();
//
//                        enumItemToAdd.setEnumItemName(JStringUtils.underlineToCamelCapitalized(enumItem.getItemName()));
//                        enumItemToAdd.setEnumItemNameUpper(StringUtils.upperCase(enumItem.getItemName()));
//                        enumItemToAdd.setEnumItemValue(enumItem.getItemValue());
//                        enumItemToAdd.setEnumItemDesc(enumItem.getItemDesc());
//
//                        enumItems.add(enumItemToAdd);
//                    }
//
//                    TableInfo.EnumClass enumClass = new TableInfo.EnumClass();
//                    enumClass.setEnumJavaNameCapitalized(JStringUtils.underlineToCamelCapitalized(column.getEnums().getEnumName()));
//                    enumClass.setEnumJavaNameUncapitalized(JStringUtils.underlineToCamelUncapitalized(column.getEnums().getEnumName()));
//                    enumClass.setEnumDesc(column.getEnums().getEnumDesc());
//                    enumClass.setEnumItems(enumItems);
//
//                    enumClasses.add(enumClass);
//
//                }
//
//                if (column.getJavaType() == JavaType.OBJECT) {
//                    field.setFieldType(JStringUtils.underlineToCamelCapitalized(column.getObjects().get(0).getObjName()));
//
//
//                    for (TableRoot.ObjectRoot objectRoot : column.getObjects()) {
//
//                        TableInfo.InnerClass innerClass = new TableInfo.InnerClass();
//                        innerClass.setInnerClassDesc(objectRoot.getObjDesc());
//                        innerClass.setInnerClassDesc(objectRoot.getObjDesc());
//
//                        List<TableInfo.Field> objectFields = new ArrayList<>();
//
//                        for (TableRoot.ObjectField objectRootField : objectRoot.getFields()) {
//                            TableInfo.Field fieldToAdd = new TableInfo.Field();
//                            fieldToAdd.setFieldType("String");
//                            fieldToAdd.setFieldDesc(objectRootField.getFieldDesc());
//                            fieldToAdd.setFieldNameCapitalized(JStringUtils.underlineToCamelCapitalized(objectRootField.getFieldName()));
//                            fieldToAdd.setFieldNameUncapitalized(JStringUtils.underlineToCamelUncapitalized(objectRootField.getFieldName()));
//                        }
//
//                        innerClass.setInnerClassFields(objectFields);
//                    }
//                }
//
//                fields.add(field);
//            }
//
//            tableInfo.setEntityFields(fields);
//            tableInfo.setInnerClasses(innerClasses);
//            tableInfo.setEnums(enumClasses);
//
//            result.add(tableInfo);
//        }
//
//
//        return result;
//    }
//
////    public void generateYaml() {
////
////        List<Table> tables = tableService.getOriginTableInfos();
////
////        List<TableRoot> tableRootInfosInDb = tableService.getTableRootInfos(tables);
////
////
////        for (TableRoot tableRoot : tableRootInfosInDb) {
////
////            YamlUtils.dumpObject(tableRoot,
////                    Paths.get(projectConfig.getTempDir(), toolsConfig.getProjectName()),
////                    tableRoot.getTblName() + ".yml");
////        }
////    }
//
//    public void generateJavaCode() {
//
//        List<TableRoot> tableRoots = YamlUtils.loadObject();
//
//        for (TableInfo tableInfo : getTableInfos(tableRoots)) {
//            // 渲染entity
//            TemplateUtils.renderTpl("entity.ftl",
//                    tableInfo.getTableJavaNameCapitalized() + ".java",
//                    tableInfo);
//
//            // 渲染request
//            TemplateUtils.renderTpl("entity_ids_request.ftl",
//                    String.format("%sIdsRequest.java", tableInfo.getTableJavaNameCapitalized()),
//                    tableInfo);
//
//            TemplateUtils.renderTpl("page_entity_request.ftl",
//                    String.format("Page%sRequest.java", tableInfo.getTableJavaNameCapitalized()),
//                    tableInfo);
//
//            TemplateUtils.renderTpl("create_entity_request.ftl",
//                    String.format("Create%sRequest.java", tableInfo.getTableJavaNameCapitalized()),
//                    tableInfo);
//
//            TemplateUtils.renderTpl("update_entity_request.ftl",
//                    String.format("Update%sRequest.java", tableInfo.getTableJavaNameCapitalized()),
//                    tableInfo);
//
//            // 渲染response
//            TemplateUtils.renderTpl("entity_data.ftl",
//                    String.format("%sData.java", tableInfo.getTableJavaNameCapitalized()),
//                    tableInfo);
//
//            // 渲染mapper
//            TemplateUtils.renderTpl("entity_mapper.ftl",
//                    String.format("%sMapper.java", tableInfo.getTableJavaNameCapitalized()),
//                    tableInfo);
//
//            // 渲染service
//            TemplateUtils.renderTpl("entity_service.ftl",
//                    String.format("%sService.java", tableInfo.getTableJavaNameCapitalized()),
//                    tableInfo);
//
//            TemplateUtils.renderTpl("entity_service_impl.ftl",
//                    String.format("%sServiceImpl.java", tableInfo.getTableJavaNameCapitalized()),
//                    tableInfo);
//
//            // 渲染controller
//            TemplateUtils.renderTpl("entity_controller.ftl",
//                    String.format("%sController.java", tableInfo.getTableJavaNameCapitalized()),
//                    tableInfo);
//
//            // 渲染枚举
//            for (TableInfo.EnumClass enumClass : tableInfo.getEnums()) {
//                TemplateUtils.renderTpl("enum.ftl",
//                        enumClass.getEnumJavaNameCapitalized() + ".java",
//                        enumClass);
//
//                TemplateUtils.renderTpl("enum_type_handler.ftl",
//                        enumClass.getEnumJavaNameCapitalized() + "TypeHandler.java",
//                        enumClass);
//            }
//        }
//    }
//}