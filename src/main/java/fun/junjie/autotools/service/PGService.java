package fun.junjie.autotools.service;

import fun.junjie.autotools.config.project.ProjectConfig;
import fun.junjie.autotools.config.snake.ConfigurationModelRepresenter;
import fun.junjie.autotools.constant.ToolsConfig;
import fun.junjie.autotools.domain.postgre.Column;
import fun.junjie.autotools.domain.postgre.Table;
import fun.junjie.autotools.domain.yaml.*;
import fun.junjie.autotools.domain.yaml.JavaType;
import fun.junjie.autotools.utils.YamlUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;
import java.util.regex.Matcher;

@SuppressWarnings({"Duplicates", "AlibabaMethodTooLong"})
@Service
@RequiredArgsConstructor
public class PGService {


    private final JdbcTemplate jdbcTemplate;

    /**
     * 从数据库中获取表、列信息
     */
    private List<Table> getOriginTableInfos() {
        if (this.jdbcTemplate.getDataSource() == null) {
            throw new RuntimeException("数据库链接错误");
        }

        try {
            Map<String, Table> tableName2TableMap = new HashMap<>(20);

            DatabaseMetaData dbMetaData = this.jdbcTemplate.getDataSource().getConnection().getMetaData();


            // 处理表信息
            ResultSet tables = dbMetaData.getTables(
                    null, null,
                    ProjectConfig.getTablePrefix() + "_%", new String[]{"TABLE"});
            while (tables.next()) {

                String tableName = tables.getString("table_name");
                String tableDesc = tables.getString("remarks");

                if (!ProjectConfig.needGenerate(tableName)) {
                    continue;
                }

                tableName2TableMap.put(tableName, new Table(tableName, tableDesc));
            }

            // 处理列信息
            ResultSet columns = dbMetaData.getColumns(
                    null, null,
                    ProjectConfig.getTablePrefix() + "%", null);
            while (columns.next()) {

                String tableName = columns.getString("table_name");
                String columnName = columns.getString("column_name");
                String typeName = columns.getString("type_name");
                String remarks = columns.getString("remarks");

                if (!ProjectConfig.needGenerate(tableName)) {
                    continue;
                }

                Table table = tableName2TableMap.get(tableName);
                if (table == null) {
                    throw new RuntimeException("Data Wrong When Get Column Info");
                }

                table.addColumn(new Column(columnName, remarks, typeName));
            }

            return new ArrayList<>(tableName2TableMap.values());
        } catch (Exception e) {
            throw new RuntimeException("数据库获取数据错误");
        }
    }

    private List<TableRoot> getTableRootInfos(List<Table> tables) {
        List<TableRoot> tableRoots = new ArrayList<>();

        for (Table table : tables) {

            TableRoot tableRoot = new TableRoot(table.getTableName(), table.getTableDesc());

            for (Column column : table.getColumnList()) {

                tableRoot.addColumn(column.getColumnName(), column.getColumnDesc(), null);

                // 如果注释符合预定义规则，则该字段应该为枚举类型
                if (StringUtils.isNotBlank(column.getColumnDesc())
                        && ToolsConfig.ENUM_COMMENT_PATTERN.matcher(column.getColumnDesc()).matches()) {

                    Matcher matcher = ToolsConfig.ENUM_COMMENT_PATTERN.matcher(column.getColumnDesc());

                    if (matcher.matches()) {

                        tableRoot.addEnumRoot(column.getColumnName(), column.getColumnName(), matcher.group(1));
                        tableRoot.updateColumnDesc(column.getColumnName(), matcher.group(1));

                        boolean numberEnumFlag = true;

                        // 判断枚举是否是数字类型
                        for (String enumItem : matcher.group(2).split("，")) {
                            String[] enumItemInfos = enumItem.split("：");
                            if (!ToolsConfig.NUMBER_PATTERN.matcher(enumItemInfos[0]).matches()) {
                                numberEnumFlag = false;
                            }
                        }

                        // 更新下枚举项名
                        for (String enumItem : matcher.group(2).split("，")) {

                            String[] enumItemInfos = enumItem.split("：");

                            if (numberEnumFlag) {
                                tableRoot.addEnumItem(column.getColumnName(),
                                        "TMP_" + enumItemInfos[0],
                                        enumItemInfos[0], enumItemInfos[1]);

                            } else {
                                tableRoot.addEnumItem(column.getColumnName(),
                                        StringUtils.upperCase(enumItemInfos[0]),
                                        enumItemInfos[0], enumItemInfos[1]);
                            }
                        }

                        tableRoot.updateColumnJavaType(column.getColumnName(),
                                numberEnumFlag ? JavaType.NUMBER_ENUM : JavaType.STRING_ENUM);

                    } else {
                        throw new RuntimeException("Unknown Wrong.");
                    }

                }
                // 如果注释不符合预定义规则，则该字段不为枚举类型
                else {
                    switch (column.getColumnType()) {
                        case DATE:
                        case TIMESTAMPTZ:
                            tableRoot.updateColumnJavaType(column.getColumnName(), JavaType.DATE);
                            break;
                        case INT2:
                        case INT4:
                            tableRoot.updateColumnJavaType(column.getColumnName(), JavaType.NUMBER);
                            break;
                        case VARCHAR:
                            tableRoot.updateColumnJavaType(column.getColumnName(), JavaType.STRING);
                            break;
                        case JSONB:
                            tableRoot.updateColumnJavaType(column.getColumnName(), JavaType.OBJECT);

                            tableRoot.addObject(column.getColumnName(),
                                    StringUtils.capitalize(column.getColumnName()), column.getColumnDesc());
                            break;
                        default:
                            throw new RuntimeException("未准备的类型");
                    }
                }
            }

            tableRoots.add(tableRoot);
        }

        return tableRoots;
    }

    private void mergeTableRootInfos(List<TableRoot> tableRootsFromDb, List<TableRoot> tableRootsFromYaml) {

        Map<String, TableRoot> tblName2TableRootL = new HashMap<>();
        Map<String, TableRoot> tblName2TableRootR = new HashMap<>();

        for (TableRoot tableRoot : tableRootsFromDb) {
            tblName2TableRootL.put(tableRoot.getTblName(), tableRoot);
        }

        for (TableRoot tableRoot : tableRootsFromYaml) {
            tblName2TableRootR.put(tableRoot.getTblName(), tableRoot);
        }

        mergeTableRoot(tblName2TableRootR, tblName2TableRootL);

        for (Map.Entry<String, TableRoot> entry : tblName2TableRootL.entrySet()) {

            TableRoot tableRootR = tblName2TableRootR.get(entry.getKey());
            TableRoot tableRootL = entry.getValue();

            Map<String, TableRoot.ColumnRoot> colName2ColumnRootL = new HashMap<>();
            Map<String, TableRoot.ColumnRoot> colName2ColumnRootR = new HashMap<>();

            for (TableRoot.ColumnRoot column : tableRootR.getColumns()) {
                colName2ColumnRootR.put(column.getColName(), column);
            }

            for (TableRoot.ColumnRoot column : tableRootL.getColumns()) {
                colName2ColumnRootL.put(column.getColName(), column);
            }

            mergeColumnRoot(colName2ColumnRootL, colName2ColumnRootR);


            for (String colName : colName2ColumnRootR.keySet()) {
                TableRoot.ColumnRoot columnRootL = colName2ColumnRootL.get(colName);
                TableRoot.ColumnRoot columnRootR = colName2ColumnRootR.get(colName);

                if (columnRootL.getJavaType() == JavaType.NUMBER_ENUM) {
                    if (columnRootR.getJavaType() == JavaType.NUMBER_ENUM) {
                        mergeEnumRoot(columnRootL.getEnums(), columnRootR.getEnums());
                    }
                }

                if (columnRootL.getJavaType() == JavaType.STRING_ENUM) {
                    if (columnRootR.getJavaType() == JavaType.STRING_ENUM) {
                        mergeEnumRoot(columnRootL.getEnums(), columnRootR.getEnums());
                    }
                }
            }
        }
    }

    private void mergeTableRoot(Map<String, TableRoot> tblName2TableRootL,
                                Map<String, TableRoot> tblName2TableRootR) {
        /*
            融合算法：TableRoot：
                a.新增的tableRoot，从左边同步到右边
                b.删除的tableRoot，从左边同步到右边
                c.修改tblDesc，从左边同步到右边
         */

        // 处理新增的
        for (String tblName : tblName2TableRootL.keySet()) {
            if (!tblName2TableRootR.containsKey(tblName)) {
                tblName2TableRootR.put(tblName, tblName2TableRootR.get(tblName));
            }
        }

        // 处理删除的
        for (String tblName : tblName2TableRootR.keySet()) {
            if (!tblName2TableRootL.containsKey(tblName)) {
                tblName2TableRootR.remove(tblName);
            }
        }

        // 处理tblDesc改变的
        for (String tblName : tblName2TableRootR.keySet()) {
            TableRoot tableRootR = tblName2TableRootR.get(tblName);
            TableRoot tableRootL = tblName2TableRootL.get(tblName);

            if (!tableRootL.getTblName().equals(tableRootR.getTblName())) {
                tableRootR.setTblName(tableRootL.getTblName());
            }
        }
    }


    private void mergeColumnRoot(Map<String, TableRoot.ColumnRoot> colName2ColumnRootL,
                                 Map<String, TableRoot.ColumnRoot> colName2ColumnRootR) {
        /*
            融合算法：ColumnRoot：
                a.新增的columnRoot，从左边同步到右边
                b.删除的columnRoot，从左边同步到右边
                c.修改colDesc，从左边同步到右边
                d.修改javaType，从坐标同步到右边（只针对非枚举、Object类型）
         */

        // 处理新增的
        for (String colName : colName2ColumnRootL.keySet()) {
            if (!colName2ColumnRootR.containsKey(colName)) {
                colName2ColumnRootR.put(colName, colName2ColumnRootR.get(colName));
            }
        }

        // 处理删除的
        for (String colName : colName2ColumnRootR.keySet()) {
            if (!colName2ColumnRootL.containsKey(colName)) {
                colName2ColumnRootR.remove(colName);
            }
        }

        // 处理colDesc改变的
        for (String colName : colName2ColumnRootR.keySet()) {
            TableRoot.ColumnRoot columnRootR = colName2ColumnRootR.get(colName);
            TableRoot.ColumnRoot columnRootL = colName2ColumnRootR.get(colName);

            if (!columnRootR.getColName().equals(columnRootL.getColName())) {
                columnRootR.setColName(columnRootL.getColName());
            }
        }

        // 处理javaType改动
        for (String colName : colName2ColumnRootR.keySet()) {
            TableRoot.ColumnRoot columnRootR = colName2ColumnRootR.get(colName);
            TableRoot.ColumnRoot columnRootL = colName2ColumnRootR.get(colName);

            // 只有左右两边都是简单的类型时，才能够进行merge
            if (!columnRootR.getJavaType().equals(columnRootL.getJavaType())) {
                if (columnRootR.getJavaType().equals(JavaType.DATE)
                        || columnRootR.getJavaType().equals(JavaType.NUMBER)
                        || columnRootR.getJavaType().equals(JavaType.STRING)) {
                    if (columnRootL.getJavaType().equals(JavaType.DATE)
                            || columnRootL.getJavaType().equals(JavaType.NUMBER)
                            || columnRootL.getJavaType().equals(JavaType.STRING)) {
                        columnRootR.setJavaType(columnRootL.getJavaType());
                    }
                }
            }
        }

    }

    private void mergeEnumRoot(TableRoot.EnumRoot enumRootL,
                               TableRoot.EnumRoot enumRootR) {
        /*
            融合算法：EnumRoot
                a.修改enumDesc，从左边同步到右边
                b.新增的enumItem，从左边同步到右边
                c.删除的enumItem，从左边同步到右边
                d.修改itemName，从右边同步到左边
                e.修改itemDesc，从左边同步到右边
         */

        // enumDesc
        if (!enumRootR.getEnumDesc().equals(enumRootL.getEnumDesc())) {
            enumRootR.setEnumDesc(enumRootL.getEnumDesc());
        }


        Map<String, TableRoot.EnumItem> itemName2EnumItemL = new HashMap<>();
        Map<String, TableRoot.EnumItem> itemValue2EnumItemL = new HashMap<>();
        Map<String, TableRoot.EnumItem> itemName2EnumItemR = new HashMap<>();
        Map<String, TableRoot.EnumItem> itemValue2EnumItemR = new HashMap<>();

        for (TableRoot.EnumItem enumItem : enumRootL.getEnumItems()) {
            itemName2EnumItemL.put(enumItem.getItemName(), enumItem);
            itemValue2EnumItemL.put(enumItem.getItemValue(), enumItem);
        }

        for (TableRoot.EnumItem enumItem : enumRootR.getEnumItems()) {
            itemName2EnumItemR.put(enumItem.getItemName(), enumItem);
            itemValue2EnumItemR.put(enumItem.getItemValue(), enumItem);
        }

        // 新增enumItem
        for (String itemValue : itemValue2EnumItemL.keySet()) {
            if (!itemValue2EnumItemR.containsKey(itemValue)) {
                itemValue2EnumItemR.put(itemValue, itemName2EnumItemL.get(itemValue));
            }
        }

        // itemName
        for (String itemValue : itemValue2EnumItemR.keySet()) {
            TableRoot.EnumItem enumItemL = itemValue2EnumItemL.get(itemValue);
            TableRoot.EnumItem enumItemR = itemValue2EnumItemR.get(itemValue);

            if (!enumItemL.getItemName().equals(enumItemR.getItemName())) {
                enumItemL.setItemName(enumItemR.getItemName());
            }
        }

        // itemDesc
        for (String itemValue : itemValue2EnumItemR.keySet()) {
            TableRoot.EnumItem enumItemL = itemValue2EnumItemL.get(itemValue);
            TableRoot.EnumItem enumItemR = itemValue2EnumItemR.get(itemValue);

            if (!enumItemL.getItemDesc().equals(enumItemR.getItemDesc())) {
                enumItemR.setItemDesc(enumItemL.getItemDesc());
            }
        }
    }

    private void mergeInternalClassRoot() {

    }

    public void generateYaml() {

        ProjectConfig.init("project_dyf.yml");

        List<Table> tables = getOriginTableInfos();

        List<TableRoot> tableRootInfosInDb = getTableRootInfos(tables);
        List<TableRoot> tableRootInfosFromYaml = YamlUtils.loadObject();

        mergeTableRootInfos(tableRootInfosInDb, tableRootInfosFromYaml);

        for (TableRoot tableRoot : tableRootInfosInDb) {

            YamlUtils.dumpObject(tableRoot,
                    Paths.get(ToolsConfig.TEMP_DIR, ProjectConfig.getProjectName()),
                    tableRoot.getTblName() + ".yml");

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
//                    enumClass.setEnumName(StringUtils.capitalize(JStringUtils.underlineToCamel(enumRoot.getEnumName())));
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
//                    enumClass.getEnumName() + ".java",
//                    Collections.singletonMap("enumClass", enumClass));
//            render("enum_type_handler.flt",
//                    "D:\\Project\\dyf\\dyf-meta\\dyf-meta-core\\src\\main\\java\\com\\sdstc\\dyf\\meta\\core\\type_handler\\enums",
//                    enumClass.getEnumName() + "TypeHandler.java",
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
//                    JStringUtils.underlineToCamel(JStringUtils.removeTableNamePrefix(table.getTableName()))));
//            tableInfo.setTableJavaNameUncapitalized(StringUtils.uncapitalize(
//                    JStringUtils.underlineToCamel(JStringUtils.removeTableNamePrefix(table.getTableName()))));
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
//                    enumClass.setEnumName(StringUtils.capitalize(JStringUtils.underlineToCamel(enumRoot.getEnumName())));
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
//                    enumClass.getEnumName() + ".java",
//                    Collections.singletonMap("enumClass", enumClass));
//            render("enum_type_handler.flt",
//                    "D:\\Project\\dyf\\dyf-meta\\dyf-meta-core\\src\\main\\java\\com\\sdstc\\dyf\\meta\\core\\type_handler\\enums",
//                    enumClass.getEnumName() + "TypeHandler.java",
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
//            entityClass.setEntityName(StringUtils.capitalize(JStringUtils.underlineToCamel(table.getTableName())) + "Po");
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
//                        fieldType = StringUtils.capitalize(JStringUtils.underlineToCamel(column.getEnumRoot().getEnumName()));
//                        if (!entityClass.getPackagesToImport().contains("com.sdstc.dyf.meta.common.constant.enums." + fieldType)) {
//                            entityClass.getPackagesToImport().add("com.sdstc.dyf.meta.common.constant.enums." + fieldType);
//                        }
//                        break;
//                    case OBJECT:
//                        fieldType = StringUtils.capitalize(JStringUtils.underlineToCamel(column.getObjectRoots().get(0).getObjectName()));
//
//                        objectRoots.addAll(column.getObjectRoots());
//
//                        break;
//                    default:
//                        fieldType = "UNKNOWN";
//                        break;
//                }
//
//                EntityClass.Field field = new EntityClass.Field(JStringUtils.underlineToCamel(column.getColumnName()),
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
//                internalClass.setInternalClassName(StringUtils.capitalize(JStringUtils.underlineToCamel(objectRoot.getObjectName())));
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
//            mapperClass.setMapperName(StringUtils.capitalize(JStringUtils.underlineToCamel(table.getTableName())) + "Dao");
//            mapperClass.setEntityName(StringUtils.capitalize(JStringUtils.underlineToCamel(table.getTableName())) + "Po");
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
//            serviceClass.setEntityName(StringUtils.capitalize(JStringUtils.underlineToCamel(table.getTableName())));
//            serviceClass.setEntityChineseName(tableName2Chinese.get(oldTableName));
//            serviceClass.setServiceName(StringUtils.capitalize(JStringUtils.underlineToCamel(table.getTableName())) + "Service");
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
//            entityClass.setEntityName(StringUtils.capitalize(JStringUtils.underlineToCamel(table.getTableName())));
//            entityClass.setPackageName("com.sdstc.dyf.meta.core.po");
//            entityClass.addPackage("lombok.Data");
//            entityClass.addPackage("lombok.Builder");
//            entityClass.addPackage("lombok.EqualsAndHashCode");
//            entityClass.addPackage("lombok.NoArgsConstructor");
//            entityClass.addPackage("lombok.AllArgsConstructor");
//            entityClass.addPackage("com.baomidou.mybatisplus.annotation.TableName");
//            entityClass.addPackage("com.sdstc.scdp.mybatis.plus.po.BasePo");
//            entityClass.setEntityNameLower(JStringUtils.underlineToCamel(table.getTableName()));
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
//                        fieldType = StringUtils.capitalize(JStringUtils.underlineToCamel(column.getEnumRoot().getEnumName()));
//                        if (!entityClass.getPackagesToImport().contains("com.sdstc.dyf.meta.common.constant.enums." + fieldType)) {
//                            entityClass.getPackagesToImport().add("com.sdstc.dyf.meta.common.constant.enums." + fieldType);
//                        }
//                        break;
//                    case OBJECT:
//                        fieldType = StringUtils.capitalize(JStringUtils.underlineToCamel(column.getObjectRoots().get(0).getObjectName()));
//
//                        objectRoots.addAll(column.getObjectRoots());
//
//                        break;
//                    default:
//                        fieldType = "UNKNOWN";
//                        break;
//                }
//
//                EntityClass.Field field = new EntityClass.Field(JStringUtils.underlineToCamel(column.getColumnName()),
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
//                internalClass.setInternalClassName(StringUtils.capitalize(JStringUtils.underlineToCamel(objectRoot.getObjectName())));
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
//            entityClass.setEntityName(StringUtils.capitalize(JStringUtils.underlineToCamel(table.getTableName())));
//            entityClass.setEntityNameLower(JStringUtils.underlineToCamel(table.getTableName()));
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
//                        fieldType = StringUtils.capitalize(JStringUtils.underlineToCamel(column.getEnumRoot().getEnumName()));
//                        if (!entityClass.getPackagesToImport().contains("com.sdstc.dyf.meta.common.constant.enums." + fieldType)) {
//                            entityClass.getPackagesToImport().add("com.sdstc.dyf.meta.common.constant.enums." + fieldType);
//                        }
//                        break;
//                    case OBJECT:
//                        fieldType = StringUtils.capitalize(JStringUtils.underlineToCamel(column.getObjectRoots().get(0).getObjectName()));
//
//                        objectRoots.addAll(column.getObjectRoots());
//
//                        break;
//                    default:
//                        fieldType = "UNKNOWN";
//                        break;
//                }
//
//                EntityClass.Field field = new EntityClass.Field(JStringUtils.underlineToCamel(column.getColumnName()),
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
//                internalClass.setInternalClassName(StringUtils.capitalize(JStringUtils.underlineToCamel(objectRoot.getObjectName())));
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
