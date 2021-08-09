package fun.junjie.autotools.test;

import fun.junjie.autotools.domain.TableInfo;
import fun.junjie.autotools.domain.postgre.Table;
import fun.junjie.autotools.utils.TemplateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class TemplateUtilsTest {


    @Autowired
    private GenerateService generateService;
    @Autowired
    private TableUtils tableUtils;

    private static final String TEMPLATE_NAME = "controller_method_create_entity.ftl";

    @Test
    void test() {

        List<Table> tables = tableUtils.getTables();
        List<TableInfo> tableInfos = generateService.getTableInfoFromTable(tables);

        for (TableInfo tableInfo : tableInfos) {
            TemplateUtils.renderTplString(TEMPLATE_NAME, tableInfo);
        }
    }

}
