package fun.junjie.autotools.test;

import fun.junjie.autotools.config.ProjectConfig;
import fun.junjie.autotools.config.TableConfig;
import fun.junjie.autotools.config.ToolsConfig;
import fun.junjie.autotools.domain.postgre.Column;
import fun.junjie.autotools.domain.postgre.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableService2 {

    private final ProjectConfig projectConfig;
    private final ToolsConfig toolsConfig;
    private final JdbcTemplate jdbcTemplate;



    /**
     * 从数据库中获取表、列信息
     */
    public List<Table> getOriginTableInfos() {
        try {
            if (this.jdbcTemplate.getDataSource() == null) {
                throw new RuntimeException("数据库链接错误");
            }

            Map<String, Table> tableName2TableMap = new HashMap<>(20);

            DatabaseMetaData dbMetaData = this.jdbcTemplate.getDataSource().getConnection().getMetaData();

            for (TableConfig tableConfig : toolsConfig.getTableConfigs()) {

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


}
