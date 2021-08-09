package fun.junjie.autotools.test;

import fun.junjie.autotools.config.ProjectConfig;
import fun.junjie.autotools.config.TableConfig;
import fun.junjie.autotools.config.GeneratorConfig;
import fun.junjie.autotools.domain.*;
import fun.junjie.autotools.domain.postgre.Column;
import fun.junjie.autotools.domain.postgre.ColumnType;
import fun.junjie.autotools.domain.postgre.Table;
import fun.junjie.autotools.utils.JStringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fun.junjie.autotools.utils.JStringUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("Duplicates")
public class GenerateService {

    private final GeneratorConfig generatorConfig;

    private final ProjectConfig projectConfig;

    private Pattern enumCommentPattern;
    private Pattern numberPattern;

    @PostConstruct
    private void init() {
        enumCommentPattern = Pattern.compile(projectConfig.getEnumCommentPattern());
        numberPattern = Pattern.compile(projectConfig.getNumberPattern());
    }

    /**
     * 根据Table列表获取TableInfo列表
     */
    public List<TableInfo> getTableInfoFromTable(List<Table> tables) {

        List<TableInfo> tableInfos = new ArrayList<>();

        for (Table table : tables) {

            // 填充tableInfo

            TableInfo tableInfo = new TableInfo();

            tableInfo.setTableName(table.getTableName());
            tableInfo.setTableComment(table.getTableComment());

            tableInfo.setEntityName(generatorConfig.getEntityName(table.getTableName()) == null ?
                    table.getTableComment() :
                    generatorConfig.getEntityName(table.getTableName()));
            tableInfo.setBeanClass(
                    underlineToCamelCapitalized(removeTableNamePrefix(table.getTableName())));
            tableInfo.setBeanObject(
                    underlineToCamelUncapitalized(removeTableNamePrefix(table.getTableName())));
            tableInfo.setEnumInfos(new ArrayList<>());
            tableInfo.setColumnInfos(new ArrayList<>());
            tableInfo.setInternalClassInfos(new ArrayList<>());

            for (Column column : table.getColumns()) {

                ColumnInfo columnInfo = new ColumnInfo();

                columnInfo.setColumnName(column.getColumnName());
                columnInfo.setColumnType(column.getColumnType());
                columnInfo.setColumnComment(column.getColumnComment());
                columnInfo.setBeanClass(JStringUtils.underlineToCamelCapitalized(column.getColumnName()));
                columnInfo.setBeanObject(JStringUtils.underlineToCamelUncapitalized(column.getColumnName()));

                // 如果注释符合预定义规则，则该字段应该为枚举类型
                if (StringUtils.isNotBlank(column.getColumnComment())
                        && enumCommentPattern.matcher(column.getColumnComment()).matches()) {
                    EnumInfo enumInfo =
                            disposeEnumColumnComment(column.getColumnName(), column.getColumnComment());
                    tableInfo.getEnumInfos().add(enumInfo);
                    columnInfo.setEnumInfo(enumInfo);
                }
                // 如果类类型为JSONB，这需要生成内部类信息
                else if (column.getColumnType().equals(ColumnType.JSONB)) {
                    InternalClassInfo internalClassInfo =
                            disposeJsonbColumnType(column.getColumnName(), column.getColumnComment());
                    tableInfo.getInternalClassInfos().add(internalClassInfo);
                    columnInfo.setFieldType(internalClassInfo.getInternalClassName());
                    columnInfo.setInternalClassInfo(internalClassInfo);
                }
                // 如果注释不符合预定义规则，则该字段不为枚举类型
                else {
                    switch (column.getColumnType()) {
                        case DATE:
                            columnInfo.setFieldType("LocalDateTime");
                            break;
                        case VARCHAR:
                            columnInfo.setFieldType("String");
                            break;
                        case INT:
                            columnInfo.setFieldType("Integer");
                            break;
                        default:
                            throw new RuntimeException("No Ready For This Column Type");
                    }
                }

                tableInfo.getColumnInfos().add(columnInfo);
            }

            tableInfos.add(tableInfo);
        }

        return tableInfos;
    }

    private TableConfig getTableConfig(String tableName) {
        for (TableConfig tableConfig : generatorConfig.getTableConfigs()) {
            if (tableConfig.getTableName().equals(tableName)) {
                return tableConfig;
            }
        }

        throw new RuntimeException("Wrong When Find TableConfig");
    }

    private EnumInfo disposeEnumColumnComment(String columnName, String columnComment) {

        Matcher enumColumnMatcher = enumCommentPattern.matcher(columnComment);

        if (!enumColumnMatcher.matches()) {
            throw new RuntimeException("Wrong When Parse ColumnComment");
        }

        EnumInfo enumInfo = new EnumInfo();

        enumInfo.setEnumClass(JStringUtils.underlineToCamelCapitalized(columnName));
        enumInfo.setEnumObject(JStringUtils.underlineToCamelUncapitalized(columnName));
        enumInfo.setEnumComment(enumColumnMatcher.group(1));
        enumInfo.setEnumItems(new ArrayList<>());

        List<EnumItemInfo> enumItemInfos = new ArrayList<>();

        // 判断枚举是否是数字类型
        boolean numberEnumFlag = true;
        for (String enumItem : enumColumnMatcher.group(2).split("，")) {
            String[] enumItems = enumItem.split("：");
            if (!numberPattern.matcher(enumItems[0]).matches()) {
                numberEnumFlag = false;
            }
        }

        // 填充枚举项信息
        for (String enumItem : enumColumnMatcher.group(2).split("，")) {
            String[] enumItems = enumItem.split("：");

            EnumItemInfo enumItemInfo = new EnumItemInfo();

            enumItemInfo.setEnumItemValue(enumItems[0]);

            if (numberEnumFlag) {
                enumItemInfo.setEnumItemName("TMP_" + enumItems[0]);
                enumItemInfo.setEnumItemComment(enumItems[1]);
            } else {
                enumItemInfo.setEnumItemName(StringUtils.upperCase(enumItems[0]));
                enumItemInfo.setEnumItemComment(enumItems[1]);
            }

            enumInfo.getEnumItems().add(enumItemInfo);
        }

        enumInfo.setEnumValueType(numberEnumFlag ? "Integer" : "String");

        return enumInfo;
    }

    private InternalClassInfo disposeJsonbColumnType(String columnName, String columnComment) {
        InternalClassInfo internalClassInfo = new InternalClassInfo();

        internalClassInfo.setInternalClassName(JStringUtils.underlineToCamelCapitalized(columnName));
        internalClassInfo.setInternalObjectName(JStringUtils.underlineToCamelUncapitalized(columnName));
        internalClassInfo.setInternalClassComment(columnComment);
        internalClassInfo.setFieldItems(new ArrayList<>());

        return internalClassInfo;
    }
}
