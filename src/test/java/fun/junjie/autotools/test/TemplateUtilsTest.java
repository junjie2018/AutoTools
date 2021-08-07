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
    private TableService tableService;

    @Test
    void tmp() {

        List<Table> tables = tableService.getTables();

        List<TableInfo> tableInfos = generateService.getTableInfoFromTable(tables);

        for (TableInfo tableInfo : tableInfos) {
            TemplateUtils.renderTplString("controller_method_create_entity.ftl", tableInfo);
        }
    }

}
