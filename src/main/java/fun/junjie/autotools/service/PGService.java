package fun.junjie.autotools.service;

import fun.junjie.autotools.config.ProjectConfig;
import fun.junjie.autotools.config.tools.ToolsConfig;
import fun.junjie.autotools.domain.java.TableInfo;
import fun.junjie.autotools.domain.postgre.Column;
import fun.junjie.autotools.domain.postgre.Table;
import fun.junjie.autotools.domain.yaml.*;
import fun.junjie.autotools.domain.yaml.JavaType;
import fun.junjie.autotools.utils.JStringUtils;
import fun.junjie.autotools.utils.TemplateUtils;
import fun.junjie.autotools.utils.YamlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;

@SuppressWarnings({"Duplicates", "AlibabaMethodTooLong"})
@Slf4j
@Service
@RequiredArgsConstructor
public class PGService {


    private final TableService tableService;
    private final ProjectConfig projectConfig;
    private final ToolsConfig toolsConfig;

    private List<TableInfo> getTableInfos(List<TableRoot> tableRoots) {

        List<TableInfo> result = new ArrayList<>();

        for (TableRoot tableRoot : tableRoots) {
            TableInfo tableInfo = new TableInfo();

            tableInfo.setTableDesc(tableRoot.getTblDesc());
            tableInfo.setTableNameWithPrefix(tableRoot.getTblName());
            tableInfo.setTableNameWithoutPrefix(JStringUtils.removeTableNamePrefix(tableRoot.getTblName()));
            tableInfo.setTableJavaNameCapitalized(JStringUtils.underlineToCamelCapitalized(JStringUtils.removeTableNamePrefix(tableRoot.getTblName())));
            tableInfo.setTableJavaNameUncapitalized(JStringUtils.underlineToCamelUncapitalized(JStringUtils.removeTableNamePrefix(tableRoot.getTblName())));
            tableInfo.setEntityName(toolsConfig.getEntityName(tableRoot.getTblName()));

            List<TableInfo.EnumClass> enumClasses = new ArrayList<>();
            List<TableInfo.InnerClass> innerClasses = new ArrayList<>();

            List<TableInfo.Field> fields = new ArrayList<>();

            for (TableRoot.ColumnRoot column : tableRoot.getColumns()) {
                TableInfo.Field field = new TableInfo.Field();

                field.setFieldName(column.getColName());
                field.setFieldNameCapitalized(JStringUtils.underlineToCamelCapitalized(column.getColName()));
                field.setFieldNameUncapitalized(JStringUtils.underlineToCamelUncapitalized(column.getColName()));
                field.setFieldDesc(column.getColDesc());
                field.setIsPrimaryKey(toolsConfig.isPrimaryKey(tableInfo.getTableNameWithPrefix(), column.getColName()));
                if (column.getJavaType() == JavaType.STRING_ENUM || column.getJavaType() == JavaType.NUMBER_ENUM) {
                    field.setIsEnumType(true);
                    field.setEnumValueType(column.getJavaType() == JavaType.STRING_ENUM ? "String" : "Integer");
                } else {
                    field.setIsEnumType(false);
                }

                switch (column.getJavaType()) {
                    case DATE:
                        field.setFieldType("LocalDateTime");
                        break;
                    case NUMBER:
                        field.setFieldType("Long");
                        break;
                    case STRING:
                        field.setFieldType("String");
                        break;
                    case STRING_ENUM:
                    case NUMBER_ENUM:
                    case OBJECT:
                        break;
                    default:
                        throw new RuntimeException("Unknown JavaType");
                }

                if (column.getJavaType() == JavaType.STRING_ENUM || column.getJavaType() == JavaType.NUMBER_ENUM) {
                    field.setFieldType(JStringUtils.underlineToCamelCapitalized(column.getEnums().getEnumName()));

                    List<TableInfo.EnumItem> enumItems = new ArrayList<>();
                    for (TableRoot.EnumItem enumItem : column.getEnums().getEnumItems()) {
                        TableInfo.EnumItem enumItemToAdd = new TableInfo.EnumItem();

                        enumItemToAdd.setEnumItemName(JStringUtils.underlineToCamelCapitalized(enumItem.getItemName()));
                        enumItemToAdd.setEnumItemNameUpper(StringUtils.upperCase(enumItem.getItemName()));
                        enumItemToAdd.setEnumItemValue(enumItem.getItemValue());
                        enumItemToAdd.setEnumItemDesc(enumItem.getItemDesc());

                        enumItems.add(enumItemToAdd);
                    }

                    TableInfo.EnumClass enumClass = new TableInfo.EnumClass();
                    enumClass.setEnumJavaNameCapitalized(JStringUtils.underlineToCamelCapitalized(column.getEnums().getEnumName()));
                    enumClass.setEnumJavaNameUncapitalized(JStringUtils.underlineToCamelUncapitalized(column.getEnums().getEnumName()));
                    enumClass.setEnumDesc(column.getEnums().getEnumDesc());
                    enumClass.setEnumItems(enumItems);

                    enumClasses.add(enumClass);

                }

                if (column.getJavaType() == JavaType.OBJECT) {
                    field.setFieldType(JStringUtils.underlineToCamelCapitalized(column.getObjects().get(0).getObjName()));


                    for (TableRoot.ObjectRoot objectRoot : column.getObjects()) {

                        TableInfo.InnerClass innerClass = new TableInfo.InnerClass();
                        innerClass.setInnerClassDesc(objectRoot.getObjDesc());
                        innerClass.setInnerClassDesc(objectRoot.getObjDesc());

                        List<TableInfo.Field> objectFields = new ArrayList<>();

                        for (TableRoot.ObjectField objectRootField : objectRoot.getFields()) {
                            TableInfo.Field fieldToAdd = new TableInfo.Field();
                            fieldToAdd.setFieldType("String");
                            fieldToAdd.setFieldDesc(objectRootField.getFieldDesc());
                            fieldToAdd.setFieldNameCapitalized(JStringUtils.underlineToCamelCapitalized(objectRootField.getFieldName()));
                            fieldToAdd.setFieldNameUncapitalized(JStringUtils.underlineToCamelUncapitalized(objectRootField.getFieldName()));
                        }

                        innerClass.setInnerClassFields(objectFields);
                    }
                }

                fields.add(field);
            }

            tableInfo.setEntityFields(fields);
            tableInfo.setInnerClasses(innerClasses);
            tableInfo.setEnums(enumClasses);

            result.add(tableInfo);
        }


        return result;
    }

    public void generateYaml() {

        List<Table> tables = tableService.getOriginTableInfos();

        List<TableRoot> tableRootInfosInDb = tableService.getTableRootInfos(tables);


        for (TableRoot tableRoot : tableRootInfosInDb) {

            YamlUtils.dumpObject(tableRoot,
                    Paths.get(projectConfig.getTempDir(), toolsConfig.getProjectName()),
                    tableRoot.getTblName() + ".yml");
        }
    }

    public void generateJavaCode() {

        List<TableRoot> tableRoots = YamlUtils.loadObject();

        for (TableInfo tableInfo : getTableInfos(tableRoots)) {
            // 渲染entity
            TemplateUtils.renderTpl("entity.ftl",
                    tableInfo.getTableJavaNameCapitalized() + ".java",
                    tableInfo);

            // 渲染request
            TemplateUtils.renderTpl("entity_ids_request.ftl",
                    String.format("%sIdsRequest.java", tableInfo.getTableJavaNameCapitalized()),
                    tableInfo);

            TemplateUtils.renderTpl("page_entity_request.ftl",
                    String.format("Page%sRequest.java", tableInfo.getTableJavaNameCapitalized()),
                    tableInfo);

            TemplateUtils.renderTpl("create_entity_request.ftl",
                    String.format("Create%sRequest.java", tableInfo.getTableJavaNameCapitalized()),
                    tableInfo);

            TemplateUtils.renderTpl("update_entity_request.ftl",
                    String.format("Update%sRequest.java", tableInfo.getTableJavaNameCapitalized()),
                    tableInfo);

            // 渲染response
            TemplateUtils.renderTpl("entity_data.ftl",
                    String.format("%sData.java", tableInfo.getTableJavaNameCapitalized()),
                    tableInfo);

            // 渲染mapper
            TemplateUtils.renderTpl("entity_mapper.ftl",
                    String.format("%sMapper.java", tableInfo.getTableJavaNameCapitalized()),
                    tableInfo);

            // 渲染service
            TemplateUtils.renderTpl("entity_service.ftl",
                    String.format("%sService.java", tableInfo.getTableJavaNameCapitalized()),
                    tableInfo);

            TemplateUtils.renderTpl("entity_service_impl.ftl",
                    String.format("%sServiceImpl.java", tableInfo.getTableJavaNameCapitalized()),
                    tableInfo);

            // 渲染controller
            TemplateUtils.renderTpl("entity_controller.ftl",
                    String.format("%sController.java", tableInfo.getTableJavaNameCapitalized()),
                    tableInfo);

            // 渲染枚举
            for (TableInfo.EnumClass enumClass : tableInfo.getEnums()) {
                TemplateUtils.renderTpl("enum.ftl",
                        enumClass.getEnumJavaNameCapitalized() + ".java",
                        enumClass);

                TemplateUtils.renderTpl("enum_type_handler.ftl",
                        enumClass.getEnumJavaNameCapitalized() + "TypeHandler.java",
                        enumClass);
            }
        }
    }


    //
//    private HashMap<String, String> tableName2Chinese = new HashMap<>();
//
//    {
//        tableName2Chinese.put("t_dyf_domain_model", "领域模型");
//        tableName2Chinese.put("t_dyf_domain", "领域");
//        tableName2Chinese.put("t_dyf_domain_property", "属性");
//        tableName2Chinese.put("t_dyf_scene", "场景");
//        tableName2Chinese.put("t_dyf_domain_model_property", "领域属性与模型关联");
//    }
//
//

    //


//

//
//    private Root parseYAML(String fileName) {
//        try {
//
//            Yaml yaml = new Yaml();
//            Object yamlObj = yaml.load(new FileReader(fileName));
//
//            return JSON.parseObject(JSON.toJSONString(yamlObj), Root.class);
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private List<Entity> getEntities(Root root) {
//        List<Entity> entities = new ArrayList<>();
//
//        return entities;
//    }
//
//    private void renderEntity(Entity entity) throws IOException, TemplateException {
//        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
//
//        configuration.setDirectoryForTemplateLoading(new File("D:\\Download\\spring-demo-master\\spring-demo-master\\cn\\AutoTools\\src\\main\\resources\\templates"));
//        configuration.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_31).build());
//
//        Map<String, Entity> entityMap = Collections.singletonMap("entity", entity);
//
//        Template tmp = configuration.getTemplate("entity.ftl");
//
//        Writer out = new OutputStreamWriter(System.out);
//
//        tmp.process(entityMap, out);
//
//
//        out.flush();
//
//    }
//

//
//    public void generateJavaClass() {
//        // 将Yaml文件转换成渲染用的数据
//        Root root = parseYAML("output_main.yml");
//
//        if (root == null) {
//            throw new RuntimeException("Root解析失败");
//        }
//
//        generateEnum(root);
//        root = parseYAML("output_main.yml");
//        generateEntity(root);
//        root = parseYAML("output_main.yml");
//        generateService(root);
//        root = parseYAML("output_main.yml");
//        generateMapper(root);
//        root = parseYAML("output_main.yml");
//        generateRequest(root);
//    }
//
//    public void generateEnum(Root root) {
//
//        List<EnumClass> enumToGenerate = new ArrayList<>();
//
//        for (TableRoot table : root.getTables()) {
//            for (ColumnRoot column : table.getColumns()) {
//                if (JavaType.NUMBER_ENUM.equals(column.getJavaType())
//                        || JavaType.STRING_ENUM.equals(column.getJavaType())) {
//
//
//                    EnumRoot enumRoot = column.getEnumRoot();
//
//                    EnumClass enumClass = new EnumClass();
//                    enumClass.setPackageName("com.sdstc.dyf.meta.common.constant.enums");
//                    enumClass.addPackage("lombok.AllArgsConstructor");
//                    enumClass.addPackage("lombok.Getter");
//                    enumClass.addPackage("lombok.AccessLevel");
//
//                    enumClass.setEnumJavaNameCapitalized(StringUtils.capitalize(JStringUtils.underlineToCamelCapitalized(enumRoot.getEnumJavaNameCapitalized())));
//                    enumClass.setEnumDesc(enumRoot.getEnumDesc());
//
//                    if (JavaType.NUMBER_ENUM.equals(column.getJavaType())) {
//                        enumClass.setValueType("Integer");
//                    } else {
//                        enumClass.setValueType("String");
//                    }
//
//                    for (EnumRoot.EnumItem enumItem : enumRoot.getEnumItems()) {
//                        EnumClass.EnumItem enumItemToRender = new EnumClass.EnumItem();
//                        enumItemToRender.setEnumItemValue(enumItem.getEnumItemValue());
//                        enumItemToRender.setEnumItemDesc(enumItem.getEnumItemDesc());
//                        enumItemToRender.setEnumItemName(StringUtils.upperCase(enumItem.getEnumItemName()));
//
//                        enumClass.addEnumItem(enumItemToRender);
//                    }
//
//                    enumToGenerate.add(enumClass);
//                }
//            }
//        }
//
//        for (EnumClass enumClass : enumToGenerate) {
//            render("enum.ftl",
//                    "D:\\Project\\dyf\\dyf-meta\\dyf-meta-common\\src\\main\\java\\com\\sdstc\\dyf\\meta\\common\\constant\\enums",
//                    enumClass.getEnumJavaNameCapitalized() + ".java",
//                    Collections.singletonMap("enumClass", enumClass));
//            render("enum_type_handler.flt",
//                    "D:\\Project\\dyf\\dyf-meta\\dyf-meta-core\\src\\main\\java\\com\\sdstc\\dyf\\meta\\core\\type_handler\\enums",
//                    enumClass.getEnumJavaNameCapitalized() + "TypeHandler.java",
//                    Collections.singletonMap("enumClass", enumClass));
//        }
//
//
//    }
//
//
//    private static Map<String, String> tableName2EntityName = new HashMap<>();
//
//    static {
//        tableName2EntityName.put("t_dyf_domain_model", "领域模型");
//        tableName2EntityName.put("t_dyf_domain", "领域");
//        tableName2EntityName.put("t_dyf_domain_property", "属性");
//        tableName2EntityName.put("t_dyf_scene", "场景");
//        tableName2EntityName.put("t_dyf_domain_model_property", "领域属性与模型关联");
//    }
//
//    public void generateEnum2(Root root) {
//
//        List<TableInfo> tableInfos = new ArrayList<>();
//
//        for (TableRoot table : root.getTables()) {
//
//            TableInfo tableInfo = new TableInfo();
//            tableInfo.setTableDesc(table.getTableDesc());
//            tableInfo.setTableNameWithPrefix(table.getTableName());
//            tableInfo.setTableNameWithoutPrefix(JStringUtils.removeTableNamePrefix(table.getTableName()));
//            tableInfo.setTableJavaNameCapitalized(StringUtils.capitalize(
//                    JStringUtils.underlineToCamelCapitalized(JStringUtils.removeTableNamePrefix(table.getTableName()))));
//            tableInfo.setTableJavaNameUncapitalized(StringUtils.uncapitalize(
//                    JStringUtils.underlineToCamelCapitalized(JStringUtils.removeTableNamePrefix(table.getTableName()))));
//            tableInfo.setEntityName(tableName2EntityName.get(table.getTableName()));
//            tableInfo.setEntityPackageName(UserConfig.ENTITY_PACKAGE);
//
//
//            for (ColumnRoot column : table.getColumns()) {
//                if (JavaType.NUMBER_ENUM.equals(column.getJavaType())
//                        || JavaType.STRING_ENUM.equals(column.getJavaType())) {
//
//
//                    EnumRoot enumRoot = column.getEnumRoot();
//
//                    EnumClass enumClass = new EnumClass();
//                    enumClass.setPackageName("com.sdstc.dyf.meta.common.constant.enums");
//                    enumClass.addPackage("lombok.AllArgsConstructor");
//                    enumClass.addPackage("lombok.Getter");
//                    enumClass.addPackage("lombok.AccessLevel");
//
//                    enumClass.setEnumJavaNameCapitalized(StringUtils.capitalize(JStringUtils.underlineToCamelCapitalized(enumRoot.getEnumJavaNameCapitalized())));
//                    enumClass.setEnumDesc(enumRoot.getEnumDesc());
//
//                    if (JavaType.NUMBER_ENUM.equals(column.getJavaType())) {
//                        enumClass.setValueType("Integer");
//                    } else {
//                        enumClass.setValueType("String");
//                    }
//
//                    for (EnumRoot.EnumItem enumItem : enumRoot.getEnumItems()) {
//                        EnumClass.EnumItem enumItemToRender = new EnumClass.EnumItem();
//                        enumItemToRender.setEnumItemValue(enumItem.getEnumItemValue());
//                        enumItemToRender.setEnumItemDesc(enumItem.getEnumItemDesc());
//                        enumItemToRender.setEnumItemName(StringUtils.upperCase(enumItem.getEnumItemName()));
//
//                        enumClass.addEnumItem(enumItemToRender);
//                    }
//
//                    enumToGenerate.add(enumClass);
//                }
//            }
//        }
//
//        for (EnumClass enumClass : enumToGenerate) {
//            render("enum.ftl",
//                    "D:\\Project\\dyf\\dyf-meta\\dyf-meta-common\\src\\main\\java\\com\\sdstc\\dyf\\meta\\common\\constant\\enums",
//                    enumClass.getEnumJavaNameCapitalized() + ".java",
//                    Collections.singletonMap("enumClass", enumClass));
//            render("enum_type_handler.flt",
//                    "D:\\Project\\dyf\\dyf-meta\\dyf-meta-core\\src\\main\\java\\com\\sdstc\\dyf\\meta\\core\\type_handler\\enums",
//                    enumClass.getEnumJavaNameCapitalized() + "TypeHandler.java",
//                    Collections.singletonMap("enumClass", enumClass));
//        }
//
//
//    }
//
//    public void generateEntity(Root root) {
//        List<EntityClass> entityClasses = new ArrayList<>();
//
//        for (TableRoot table : root.getTables()) {
//            EntityClass entityClass = new EntityClass();
//
//            entityClass.setTableName(table.getTableName());
//
//            if (table.getTableName().startsWith(UserConfig.TABLE_PREFIX)) {
//                table.setTableName(
//                        table.getTableName().substring(UserConfig.TABLE_PREFIX.length()));
//            }
//
//            entityClass.setEntityName(StringUtils.capitalize(JStringUtils.underlineToCamelCapitalized(table.getTableName())) + "Po");
//            entityClass.setPackageName("com.sdstc.dyf.meta.core.po");
//            entityClass.addPackage("lombok.Data");
//            entityClass.addPackage("lombok.Builder");
//            entityClass.addPackage("lombok.EqualsAndHashCode");
//            entityClass.addPackage("lombok.NoArgsConstructor");
//            entityClass.addPackage("lombok.AllArgsConstructor");
//            entityClass.addPackage("com.baomidou.mybatisplus.annotation.TableName");
//            entityClass.addPackage("com.sdstc.scdp.mybatis.plus.po.BasePo");
//
//            List<ObjectRoot> objectRoots = new ArrayList<>();
//

//
//                EntityClass.Field field = new EntityClass.Field(JStringUtils.underlineToCamelCapitalized(column.getColumnName()),
//                        fieldType,
//                        column.getColumnDesc());
//
//                if (column.getJavaType().equals(JavaType.OBJECT)) {
//                    field.getAnnotations().add(String.format("@TableField(typeHandler = %sHandler.class)", field.getFieldType()));
//                    if (!entityClass.getPackagesToImport().contains("com.baomidou.mybatisplus.annotation.TableField")) {
//                        entityClass.getPackagesToImport().add("com.baomidou.mybatisplus.annotation.TableField");
//                    }
//                }
//
//                entityClass.addField(field);
//            }
//
//
//            List<EntityClass.InternalClass> internalClasses = new ArrayList<>();
//            List<String> internalClassHandlers = new ArrayList<>();
//
//            for (ObjectRoot objectRoot : objectRoots) {
//                EntityClass.InternalClass internalClass = new EntityClass.InternalClass();
//                internalClass.setInternalClassDesc(objectRoot.getObjectDesc());
//                internalClass.setInternalClassName(StringUtils.capitalize(JStringUtils.underlineToCamelCapitalized(objectRoot.getObjectName())));
//
//                for (ObjectRoot.FieldItem fieldItem : objectRoot.getFieldItems()) {
//                    String fieldType;
//                    switch (fieldItem.getFieldType()) {
//                        case DATE:
//                            fieldType = "LocalDateTime";
//                            break;
//                        case NUMBER:
//                            fieldType = "Long";
//                            break;
//                        case STRING:
//                            fieldType = "String";
//                            break;
//                        default:
//                            fieldType = "UNKNOWN";
//                            break;
//                    }
//
//                    internalClass.addField(fieldItem.getFieldName(), fieldType, fieldItem.getFieldDesc());
//                }
//
//                internalClasses.add(internalClass);
//                internalClassHandlers.add(internalClass.getInternalClassName());
//
//                if (!entityClass.getPackagesToImport().contains("com.sdstc.dyf.meta.core.handler.JsonTypeHandler")) {
//                    entityClass.getPackagesToImport().add("com.sdstc.dyf.meta.core.handler.JsonTypeHandler");
//                    entityClass.getPackagesToImport().add("lombok.extern.slf4j.Slf4j");
//                    entityClass.getPackagesToImport().add("org.apache.ibatis.type.JdbcType");
//                    entityClass.getPackagesToImport().add("org.apache.ibatis.type.MappedJdbcTypes");
//                    entityClass.getPackagesToImport().add("org.apache.ibatis.type.MappedTypes");
//                }
//            }
//
//            entityClasses.add(entityClass);
//            entityClass.setInternalClasses(internalClasses);
//            entityClass.setInternalClassHandlers(internalClassHandlers);
//        }
//
//        for (EntityClass entityClass : entityClasses) {
//            render("entity.ftl",
//                    "D:\\Project\\dyf\\dyf-meta\\dyf-meta-core\\src\\main\\java\\com\\sdstc\\dyf\\meta\\core\\po",
//                    entityClass.getEntityName() + ".java",
//                    Collections.singletonMap("entityClass", entityClass));
//        }
//
//
//    }
//
//    public void generateMapper(Root root) {
//        List<MapperClass> mapperClasses = new ArrayList<>();
//        for (TableRoot table : root.getTables()) {
//
//            if (table.getTableName().startsWith(UserConfig.TABLE_PREFIX)) {
//                table.setTableName(
//                        table.getTableName().substring(UserConfig.TABLE_PREFIX.length()));
//            }
//
//            MapperClass mapperClass = new MapperClass();
//            mapperClass.setPackageName("com.sdstc.dyf.meta.core.dao");
//            mapperClass.setMapperName(StringUtils.capitalize(JStringUtils.underlineToCamelCapitalized(table.getTableName())) + "Dao");
//            mapperClass.setEntityName(StringUtils.capitalize(JStringUtils.underlineToCamelCapitalized(table.getTableName())) + "Po");
//
//            mapperClass.getPackagesToImport().add("com.sdstc.scdp.mybatis.plus.GeneralDao");
//            mapperClass.getPackagesToImport().add("com.sdstc.dyf.meta.core.po." + mapperClass.getEntityName());
//
//
//            mapperClasses.add(mapperClass);
//        }
//
//        for (MapperClass mapperClass : mapperClasses) {
//            render("mapper.ftl",
//                    "D:\\Project\\dyf\\dyf-meta\\dyf-meta-core\\src\\main\\java\\com\\sdstc\\dyf\\meta\\core\\dao",
//                    mapperClass.getMapperName() + ".java",
//                    Collections.singletonMap("mapperClass", mapperClass));
//        }
//    }
//
//    public void generateService(Root root) {
//
//        List<ServiceClass> serviceClasses = new ArrayList<>();
//
//        for (TableRoot table : root.getTables()) {
//
////            if ("t_dyf_domain_model_property".equals(table.getTableName())) {
////                continue;
////            }
//
//            String oldTableName = table.getTableName();
//
//            if (table.getTableName().startsWith(UserConfig.TABLE_PREFIX)) {
//                table.setTableName(
//                        table.getTableName().substring(UserConfig.TABLE_PREFIX.length()));
//            }
//
//            ServiceClass serviceClass = new ServiceClass();
//
//            serviceClass.setEntityName(StringUtils.capitalize(JStringUtils.underlineToCamelCapitalized(table.getTableName())));
//            serviceClass.setEntityChineseName(tableName2Chinese.get(oldTableName));
//            serviceClass.setServiceName(StringUtils.capitalize(JStringUtils.underlineToCamelCapitalized(table.getTableName())) + "Service");
//
//            serviceClasses.add(serviceClass);
//        }
//
//        List<EntityClass> entityClasses = new ArrayList<>();
//
//        for (TableRoot table : root.getTables()) {
//            EntityClass entityClass = new EntityClass();
//
//            entityClass.setTableName(table.getTableName());
//
////            if ("t_dyf_domain_model_property".equals(table.getTableName())) {
////                continue;
////            }
//
//            if (table.getTableName().startsWith(UserConfig.TABLE_PREFIX)) {
//                table.setTableName(
//                        table.getTableName().substring(UserConfig.TABLE_PREFIX.length()));
//            }
//
//            entityClass.setEntityName(StringUtils.capitalize(JStringUtils.underlineToCamelCapitalized(table.getTableName())));
//            entityClass.setPackageName("com.sdstc.dyf.meta.core.po");
//            entityClass.addPackage("lombok.Data");
//            entityClass.addPackage("lombok.Builder");
//            entityClass.addPackage("lombok.EqualsAndHashCode");
//            entityClass.addPackage("lombok.NoArgsConstructor");
//            entityClass.addPackage("lombok.AllArgsConstructor");
//            entityClass.addPackage("com.baomidou.mybatisplus.annotation.TableName");
//            entityClass.addPackage("com.sdstc.scdp.mybatis.plus.po.BasePo");
//            entityClass.setEntityNameLower(JStringUtils.underlineToCamelCapitalized(table.getTableName()));
//
//            List<ObjectRoot> objectRoots = new ArrayList<>();
//
//            for (ColumnRoot column : table.getColumns()) {
//                String fieldType;
//                switch (column.getJavaType()) {
//                    case DATE:
//                        fieldType = "LocalDateTime";
//                        break;
//                    case NUMBER:
//                        fieldType = "Long";
//                        break;
//                    case STRING:
//                        fieldType = "String";
//                        break;
//                    case STRING_ENUM:
//                    case NUMBER_ENUM:
//                        fieldType = StringUtils.capitalize(JStringUtils.underlineToCamelCapitalized(column.getEnumRoot().getEnumJavaNameCapitalized()));
//                        if (!entityClass.getPackagesToImport().contains("com.sdstc.dyf.meta.common.constant.enums." + fieldType)) {
//                            entityClass.getPackagesToImport().add("com.sdstc.dyf.meta.common.constant.enums." + fieldType);
//                        }
//                        break;
//                    case OBJECT:
//                        fieldType = StringUtils.capitalize(JStringUtils.underlineToCamelCapitalized(column.getObjectRoots().get(0).getObjectName()));
//
//                        objectRoots.addAll(column.getObjectRoots());
//
//                        break;
//                    default:
//                        fieldType = "UNKNOWN";
//                        break;
//                }
//
//                EntityClass.Field field = new EntityClass.Field(JStringUtils.underlineToCamelCapitalized(column.getColumnName()),
//                        fieldType,
//                        column.getColumnDesc());
//
//                if (column.getJavaType().equals(JavaType.OBJECT)) {
//                    field.getAnnotations().add(String.format("@TableField(typeHandler = %sHandler.class)", field.getFieldType()));
//                    if (!entityClass.getPackagesToImport().contains("com.baomidou.mybatisplus.annotation.TableField")) {
//                        entityClass.getPackagesToImport().add("com.baomidou.mybatisplus.annotation.TableField");
//                    }
//                }
//
//                entityClass.addField(field);
//            }
//
//
//            List<EntityClass.InternalClass> internalClasses = new ArrayList<>();
//            List<String> internalClassHandlers = new ArrayList<>();
//
//            for (ObjectRoot objectRoot : objectRoots) {
//                EntityClass.InternalClass internalClass = new EntityClass.InternalClass();
//                internalClass.setInternalClassDesc(objectRoot.getObjectDesc());
//                internalClass.setInternalClassName(StringUtils.capitalize(JStringUtils.underlineToCamelCapitalized(objectRoot.getObjectName())));
//
//                for (ObjectRoot.FieldItem fieldItem : objectRoot.getFieldItems()) {
//                    String fieldType;
//                    switch (fieldItem.getFieldType()) {
//                        case DATE:
//                            fieldType = "LocalDateTime";
//                            break;
//                        case NUMBER:
//                            fieldType = "Long";
//                            break;
//                        case STRING:
//                            fieldType = "String";
//                            break;
//                        default:
//                            fieldType = "UNKNOWN";
//                            break;
//                    }
//
//                    internalClass.addField(fieldItem.getFieldName(), fieldType, fieldItem.getFieldDesc());
//                }
//
//                internalClasses.add(internalClass);
//                internalClassHandlers.add(internalClass.getInternalClassName());
//
//                if (!entityClass.getPackagesToImport().contains("com.sdstc.dyf.meta.core.handler.JsonTypeHandler")) {
//                    entityClass.getPackagesToImport().add("com.sdstc.dyf.meta.core.handler.JsonTypeHandler");
//                    entityClass.getPackagesToImport().add("lombok.extern.slf4j.Slf4j");
//                    entityClass.getPackagesToImport().add("org.apache.ibatis.type.JdbcType");
//                    entityClass.getPackagesToImport().add("org.apache.ibatis.type.MappedJdbcTypes");
//                    entityClass.getPackagesToImport().add("org.apache.ibatis.type.MappedTypes");
//                }
//            }
//
//            entityClasses.add(entityClass);
//            entityClass.setInternalClasses(internalClasses);
//            entityClass.setInternalClassHandlers(internalClassHandlers);
//        }
//
//        for (int i = 0; i < entityClasses.size(); i++) {
//            ServiceClass serviceClass = serviceClasses.get(i);
//            EntityClass entityClass = entityClasses.get(i);
//
//            render("service.ftl",
//                    "D:\\Project\\dyf\\dyf-meta\\dyf-meta-core\\src\\main\\java\\com\\sdstc\\dyf\\meta\\core\\service",
//                    serviceClass.getServiceName() + ".java",
//                    Collections.singletonMap("serviceClass", serviceClass));
//
//            Map<String, Object> renderData = new HashMap<>();
//            renderData.put("serviceClass", serviceClass);
//            renderData.put("entityClass", entityClass);
//
//            render("service_impl.ftl",
//                    "D:\\Project\\dyf\\dyf-meta\\dyf-meta-core\\src\\main\\java\\com\\sdstc\\dyf\\meta\\core\\service\\impl",
//                    serviceClass.getServiceName() + "Impl.java",
//                    renderData);
//
//
//            render("controller.ftl",
//                    "D:\\Project\\dyf\\dyf-meta\\dyf-meta-core\\src\\main\\java\\com\\sdstc\\dyf\\meta\\core\\controller",
//                    entityClass.getEntityName() + "Controller.java",
//                    Collections.singletonMap("entityClass", entityClass));
//
//        }
//    }
//
//    public void generateRequest(Root root) {
//
//        List<EntityClass> entityClasses = new ArrayList<>();
//
//        for (TableRoot table : root.getTables()) {
//
////            if ("t_dyf_domain_model_property".equals(table.getTableName())) {
////                continue;
////            }
//
//            String oldTableName = table.getTableName();
//
//            EntityClass entityClass = new EntityClass();
//
//            entityClass.setTableName(table.getTableName());
//
//            if (table.getTableName().startsWith(UserConfig.TABLE_PREFIX)) {
//                table.setTableName(
//                        table.getTableName().substring(UserConfig.TABLE_PREFIX.length()));
//            }
//
//            entityClass.setEntityName(StringUtils.capitalize(JStringUtils.underlineToCamelCapitalized(table.getTableName())));
//            entityClass.setEntityNameLower(JStringUtils.underlineToCamelCapitalized(table.getTableName()));
//            entityClass.setPackageName("com.sdstc.dyf.meta.core.po");
//            entityClass.addPackage("lombok.Data");
//            entityClass.setEntityChineseName(tableName2Chinese.get(oldTableName));
//
//            List<ObjectRoot> objectRoots = new ArrayList<>();
//
//            for (ColumnRoot column : table.getColumns()) {
//                String fieldType;
//                switch (column.getJavaType()) {
//                    case DATE:
//                        fieldType = "LocalDateTime";
//                        break;
//                    case NUMBER:
//                        fieldType = "Long";
//                        break;
//                    case STRING:
//                        fieldType = "String";
//                        break;
//                    case STRING_ENUM:
//                    case NUMBER_ENUM:
//                        fieldType = StringUtils.capitalize(JStringUtils.underlineToCamelCapitalized(column.getEnumRoot().getEnumJavaNameCapitalized()));
//                        if (!entityClass.getPackagesToImport().contains("com.sdstc.dyf.meta.common.constant.enums." + fieldType)) {
//                            entityClass.getPackagesToImport().add("com.sdstc.dyf.meta.common.constant.enums." + fieldType);
//                        }
//                        break;
//                    case OBJECT:
//                        fieldType = StringUtils.capitalize(JStringUtils.underlineToCamelCapitalized(column.getObjectRoots().get(0).getObjectName()));
//
//                        objectRoots.addAll(column.getObjectRoots());
//
//                        break;
//                    default:
//                        fieldType = "UNKNOWN";
//                        break;
//                }
//
//                EntityClass.Field field = new EntityClass.Field(JStringUtils.underlineToCamelCapitalized(column.getColumnName()),
//                        fieldType,
//                        column.getColumnDesc());
//
//                if (column.getJavaType().equals(JavaType.OBJECT)) {
//                    field.getAnnotations().add(String.format("@TableField(typeHandler = %sHandler.class)", field.getFieldType()));
//                    if (!entityClass.getPackagesToImport().contains("com.baomidou.mybatisplus.annotation.TableField")) {
//
//                    }
//                }
//
//                entityClass.addField(field);
//            }
//
//
//            List<EntityClass.InternalClass> internalClasses = new ArrayList<>();
//            List<String> internalClassHandlers = new ArrayList<>();
//
//            for (ObjectRoot objectRoot : objectRoots) {
//                EntityClass.InternalClass internalClass = new EntityClass.InternalClass();
//                internalClass.setInternalClassDesc(objectRoot.getObjectDesc());
//                internalClass.setInternalClassName(StringUtils.capitalize(JStringUtils.underlineToCamelCapitalized(objectRoot.getObjectName())));
//
//                for (ObjectRoot.FieldItem fieldItem : objectRoot.getFieldItems()) {
//                    String fieldType;
//                    switch (fieldItem.getFieldType()) {
//                        case DATE:
//                            fieldType = "LocalDateTime";
//                            break;
//                        case NUMBER:
//                            fieldType = "Long";
//                            break;
//                        case STRING:
//                            fieldType = "String";
//                            break;
//                        default:
//                            fieldType = "UNKNOWN";
//                            break;
//                    }
//
//                    internalClass.addField(fieldItem.getFieldName(), fieldType, fieldItem.getFieldDesc());
//                }
//
//                internalClasses.add(internalClass);
//                internalClassHandlers.add(internalClass.getInternalClassName());
//
//                if (!entityClass.getPackagesToImport().contains("com.sdstc.dyf.meta.core.handler.JsonTypeHandler")) {
//
//                }
//            }
//
//            entityClasses.add(entityClass);
//            entityClass.setInternalClasses(internalClasses);
//            entityClass.setInternalClassHandlers(internalClassHandlers);
//        }
//
//        for (EntityClass entityClass : entityClasses) {
//            render("request\\create_domain_request.ftl",
//                    "D:\\Project\\dyf\\dyf-meta\\dyf-meta-common\\src\\main\\java\\com\\sdstc\\dyf\\meta\\common\\request",
//                    "Create" + entityClass.getEntityName() + "Request.java",
//                    Collections.singletonMap("entityClass", entityClass));
//            render("request\\domain_ids_request.ftl",
//                    "D:\\Project\\dyf\\dyf-meta\\dyf-meta-common\\src\\main\\java\\com\\sdstc\\dyf\\meta\\common\\request",
//                    entityClass.getEntityName() + "IdsRequest.java",
//                    Collections.singletonMap("entityClass", entityClass));
//            render("request\\page_domains_request.ftl",
//                    "D:\\Project\\dyf\\dyf-meta\\dyf-meta-common\\src\\main\\java\\com\\sdstc\\dyf\\meta\\common\\request",
//                    "Page" + entityClass.getEntityName() + "sRequest.java",
//                    Collections.singletonMap("entityClass", entityClass));
//            render("request\\update_domain_request.ftl",
//                    "D:\\Project\\dyf\\dyf-meta\\dyf-meta-common\\src\\main\\java\\com\\sdstc\\dyf\\meta\\common\\request",
//                    "Update" + entityClass.getEntityName() + "Request.java",
//                    Collections.singletonMap("entityClass", entityClass));
//            render("data\\domain_data.ftl",
//                    "D:\\Project\\dyf\\dyf-meta\\dyf-meta-common\\src\\main\\java\\com\\sdstc\\dyf\\meta\\common\\data",
//                    entityClass.getEntityName() + "Data.java",
//                    Collections.singletonMap("entityClass", entityClass));
//        }
//
}
