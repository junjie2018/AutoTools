package fun.junjie.autotools.test;

import fun.junjie.autotools.config.TableConfig;
import fun.junjie.autotools.config.GeneratorConfig;
import fun.junjie.autotools.constant.JdbcLabel;
import fun.junjie.autotools.constant.TableType;
import fun.junjie.autotools.domain.postgre.Column;
import fun.junjie.autotools.domain.postgre.Table;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fun.junjie.autotools.utils.AssertUtils.*;

@Slf4j
@Service
public class TableUtils implements ApplicationContextAware {

    private static JdbcTemplate jdbcTemplate;
    private static GeneratorConfig generatorConfig;

<<<<<<< HEAD:src/main/java/fun/junjie/autotools/test/TableUtils.java
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);
        generatorConfig = applicationContext.getBean(generatorConfig.getClass());
    }

=======
>>>>>>> 92485bde88f093ec797579be2dc6099aef0698b2:src/main/java/fun/junjie/autotools/test/TableService.java
    /**
     * 从数据库中获取表、列信息
     */
    public static List<Table> getTables() {

        assertNotNull(jdbcTemplate, "获取dbcTemplate失败");
        assertNotNull(generatorConfig, "获取toolsConfig失败");
        assertNotNull(jdbcTemplate.getDataSource(), "获取数据源失败");

        DataSource dataSource = jdbcTemplate.getDataSource();

        try {
            Map<String, Table> tableName2TableMap = new HashMap<>(20);
            DatabaseMetaData databaseMetaData = dataSource.getConnection().getMetaData();

            // 真多每个配置
            for (TableConfig tableConfig : generatorConfig.getTableConfigs()) {

                // 处理表信息
                ResultSet tables = databaseMetaData.getTables(
                        null, null,
                        tableConfig.getTableName(), new String[]{TableType.TABLE});

                while (tables.next()) {

                    String tableName = tables.getString(JdbcLabel.TABLE_NAME);
                    String tableDesc = tables.getString(JdbcLabel.REMARKS);

                    tableName2TableMap.put(tableName, new Table(tableName, tableDesc));
                }

                // 处理列信息
                ResultSet columns = databaseMetaData.getColumns(
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

        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {


            return new ArrayList<>(tableName2TableMap.values());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
