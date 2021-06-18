package fun.junjie.autotools.service;

import fun.junjie.autotools.config.ProjectConfig;
import fun.junjie.autotools.config.tools.TableConfig;
import fun.junjie.autotools.config.tools.ToolsConfig;
import fun.junjie.autotools.domain.postgre.Column;
import fun.junjie.autotools.domain.postgre.Table;
import fun.junjie.autotools.domain.yaml.JavaType;
import fun.junjie.autotools.domain.yaml.TableRoot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableService {

    private final ProjectConfig projectConfig;
    private final ToolsConfig toolsConfig;
    private final JdbcTemplate jdbcTemplate;

    private static Pattern ENUM_COMMENT_PATTERN = Pattern.compile("^([\\u4e00-\\u9fa5]{1,})（(([A-Za-z0-9-]+：[\\u4e00-\\u9fa5]{1,}，?)+)）$");
    private static Pattern NUMBER_PATTERN = Pattern.compile("^[0-9]*$");

    /**
     * 从数据库中获取表、列信息
     */
    public List<Table> getOriginTableInfos() {
        if (this.jdbcTemplate.getDataSource() == null) {
            throw new RuntimeException("数据库链接错误");
        }

        try {
            Map<String, Table> tableName2TableMap = new HashMap<>(20);

            DatabaseMetaData dbMetaData = this.jdbcTemplate.getDataSource().getConnection().getMetaData();

            for (TableConfig tableConfig : toolsConfig.getTablesConfig().getTableConfig()) {

                // 处理表信息
                ResultSet tables = dbMetaData.getTables(
                        null, null,
                        tableConfig.getTableName(), new String[]{"TABLE"});

                while (tables.next()) {

                    String tableName = tables.getString("table_name");
                    String tableDesc = tables.getString("remarks");

                    tableName2TableMap.put(tableName, new Table(tableName, tableDesc));
                }

                // 处理列信息
                ResultSet columns = dbMetaData.getColumns(
                        null, null,
                        tableConfig.getTableName(), null);

                while (columns.next()) {

                    String tableName = columns.getString("table_name");
                    String columnName = columns.getString("column_name");
                    String typeName = columns.getString("type_name");
                    String remarks = columns.getString("remarks");

                    Table table = tableName2TableMap.get(tableName);
                    if (table == null) {
                        throw new RuntimeException("Data Wrong When Get Column Info");
                    }

                    table.addColumn(new Column(columnName, remarks, typeName));
                }

            }

            return new ArrayList<>(tableName2TableMap.values());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new RuntimeException("数据库获取数据错误");
        }
    }

    public List<TableRoot> getTableRootInfos(List<Table> tables) {
        List<TableRoot> tableRoots = new ArrayList<>();

        for (Table table : tables) {

            TableRoot tableRoot = new TableRoot(table.getTableName(), table.getTableDesc());

            for (Column column : table.getColumnList()) {

                tableRoot.addColumn(column.getColumnName(), column.getColumnDesc(), null);

                // 如果注释符合预定义规则，则该字段应该为枚举类型
                if (StringUtils.isNotBlank(column.getColumnDesc())
                        && ENUM_COMMENT_PATTERN.matcher(column.getColumnDesc()).matches()) {

                    Matcher matcher = ENUM_COMMENT_PATTERN.matcher(column.getColumnDesc());

                    if (matcher.matches()) {

                        tableRoot.addEnumRoot(column.getColumnName(), column.getColumnName(), matcher.group(1));
                        tableRoot.updateColumnDesc(column.getColumnName(), matcher.group(1));

                        boolean numberEnumFlag = true;

                        // 判断枚举是否是数字类型
                        for (String enumItem : matcher.group(2).split("，")) {
                            String[] enumItemInfos = enumItem.split("：");
                            if (!NUMBER_PATTERN.matcher(enumItemInfos[0]).matches()) {
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
}
