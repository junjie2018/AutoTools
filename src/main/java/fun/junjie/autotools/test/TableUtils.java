package fun.junjie.autotools.test;

import fun.junjie.autotools.config.TableConfig;
import fun.junjie.autotools.config.ToolsConfig;
import fun.junjie.autotools.constant.JdbcLabel;
import fun.junjie.autotools.constant.TableType;
import fun.junjie.autotools.domain.postgre.Column;
import fun.junjie.autotools.domain.postgre.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


@Slf4j
@Service
public class TableService implements ApplicationContextAware {

    private static JdbcTemplate jdbcTemplate;
    private static ToolsConfig toolsConfig;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);
        toolsConfig = applicationContext.getBean(toolsConfig.getClass());
    }

    /**
     * 从数据库中获取表、列信息
     */
    public static List<Table> getTables() {

        if()

        try {
            if (jdbcTemplate.getDataSource() == null) {
                throw new RuntimeException("Datasource Config Wrong");
            }

            // 收集表信息

            Map<String, Table> tableName2TableMap = new HashMap<>(20);

            DatabaseMetaData dbMetaData = this.jdbcTemplate.getDataSource().getConnection().getMetaData();

            for (TableConfig tableConfig : toolsConfig.getTableConfigs()) {

                // 处理表信息
                ResultSet tables = dbMetaData.getTables(
                        null, null,
                        tableConfig.getTableName(), new String[]{TableType.TABLE});

                while (tables.next()) {

                    String tableName = tables.getString(JdbcLabel.TABLE_NAME);
                    String tableDesc = tables.getString(JdbcLabel.REMARKS);

                    tableName2TableMap.put(tableName, new Table(tableName, tableDesc));
                }

                // 处理列信息
                ResultSet columns = dbMetaData.getColumns(
                        null, null,
                        tableConfig.getTableName(), null);

                while (columns.next()) {

                    String tableName = columns.getString(JdbcLabel.TABLE_NAME);
                    String columnName = columns.getString(JdbcLabel.COLUMN_NAME);
                    String typeName = columns.getString(JdbcLabel.TYPE_NAME);
                    String remarks = columns.getString(JdbcLabel.REMARKS);

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
            throw new RuntimeException("Datasource Config Wrong");
        }
    }


}
