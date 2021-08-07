package fun.junjie.autotools.generator;

import fun.junjie.autotools.domain.TableInfo;
import fun.junjie.autotools.domain.postgre.Table;
import fun.junjie.autotools.test.GenerateService;
import fun.junjie.autotools.test.TableService;
import fun.junjie.autotools.utils.TemplateUtilsMax;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class Generator {

    @Autowired
    private TableService tableService;

    @Autowired
    private GenerateService generateService;

    @Test
    void test() {

        List<Table> tables = tableService.getTables();
        List<TableInfo> tableInfos = generateService.getTableInfoFromTable(tables);

        TemplateUtilsMax.render(tableInfos);
    }
}
