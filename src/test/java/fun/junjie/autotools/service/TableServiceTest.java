package fun.junjie.autotools.service;

import fun.junjie.autotools.config.ProjectConfig;
import fun.junjie.autotools.config.ToolsConfig;
import fun.junjie.autotools.domain.TableInfo;
import fun.junjie.autotools.domain.postgre.Table;
import fun.junjie.autotools.utils.JsonUtils;
import fun.junjie.autotools.utils.TemplateUtilsMax;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Paths;
import java.util.List;

@SpringBootTest
class TableServiceTest {

    @Autowired
    private TableService tableService;
    @Autowired
    private ToolsConfig toolsConfig;
    @Autowired
    private ProjectConfig projectConfig;
    @Autowired
    private GenerateService generateService;

    @Test
    void test() {

//        List<TableInfo> tableInfos = JsonUtils.loadObject(Paths.get("C:\\Users\\wujj\\Desktop\\AutoTools\\src\\main\\resources\\tables\\pdm"), "core.json");

        List<Table> tables = tableService.getTables();
        List<TableInfo> tableInfos = generateService.getTableInfoFromTable(tables);

        for (TableInfo tableInfo : tableInfos) {
            TemplateUtilsMax.renderTpl("CreateEntityRequest.ftl", tableInfo);
        }

    }
}
