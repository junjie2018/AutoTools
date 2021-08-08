package fun.junjie.autotools.test;

import fun.junjie.autotools.config.ToolsConfig;
import fun.junjie.autotools.domain.EnumInfo;
import fun.junjie.autotools.domain.TableInfo;
import fun.junjie.autotools.domain.postgre.Table;
import fun.junjie.autotools.utils.JsonUtilsToArrange;
import fun.junjie.autotools.utils.ProcessUtils;
import fun.junjie.autotools.utils.TemplateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Paths;
import java.util.List;

@SpringBootTest
class TemplateConfigTest {

    @Autowired
    private GenerateService generateService;
    @Autowired
    private TableService tableService;
    @Autowired
    private ToolsConfig toolsConfig;

    @Test
    void testFileCompare() {
        List<Table> tables = tableService.getTables();
        List<TableInfo> tableInfos = generateService.getTableInfoFromTable(tables);

        JsonUtilsToArrange.dumpObject(tableInfos, Paths.get("C:\\Users\\wujj\\Desktop\\AutoTools\\src\\main\\resources\\tables\\pdm"), "tmp.json");

        ProcessUtils.compareTwoDirs(
                Paths.get("C:\\Users\\wujj\\Desktop\\AutoTools\\src\\main\\resources\\tables\\pdm\\tmp.json"),
                Paths.get("C:\\Users\\wujj\\Desktop\\AutoTools\\src\\main\\resources\\tables\\pdm\\core.json"));

    }

    @Test
    void testCreateRequestAndResponse() {
        List<TableInfo> tableInfos = JsonUtilsToArrange.loadObject(Paths.get("C:\\Users\\wujj\\Desktop\\AutoTools\\src\\main\\resources\\tables\\pdm"), "core.json");

        for (TableInfo tableInfo : tableInfos) {
            TemplateUtils.renderTpl("entity.ftl", tableInfo);
            TemplateUtils.renderTpl("create_entity_request.ftl", tableInfo);
            TemplateUtils.renderTpl("update_entity_request.ftl", tableInfo);
            TemplateUtils.renderTpl("entity_ids_request.ftl", tableInfo);
            TemplateUtils.renderTpl("page_entity_request.ftl", tableInfo);
            TemplateUtils.renderTpl("entity_data.ftl", tableInfo);
        }

        for (TableInfo tableInfo : tableInfos) {
            if (tableInfo.getEnumInfos().size() > 0) {
                for (EnumInfo enumInfo : tableInfo.getEnumInfos()) {
                    TemplateUtils.renderTpl("enum.ftl", enumInfo);
                }
            }
        }
    }

    @Test
    void testEntityController() {
        List<TableInfo> tableInfos = JsonUtilsToArrange.loadObject(Paths.get("C:\\Users\\wujj\\Desktop\\AutoTools\\src\\main\\resources\\tables\\pdm"), "core.json");

        for (TableInfo tableInfo : tableInfos) {
//            TemplateUtils.renderTpl("entity_controller.ftl", tableInfo);
//            TemplateUtils.renderTpl("entity_service.ftl", tableInfo);
            TemplateUtils.renderTpl("entity_mapper.ftl", tableInfo);
            TemplateUtils.renderTpl("entity.ftl", tableInfo);
        }
    }

    @Test
    void testIncludeAndFragment() {
        List<TableInfo> tableInfos = JsonUtilsToArrange.loadObject(Paths.get("C:\\Users\\wujj\\Desktop\\AutoTools\\src\\main\\resources\\tables\\pdm"), "core.json");

        for (TableInfo tableInfo : tableInfos) {
            TemplateUtils.renderTpl("service.ftl", tableInfo);
            TemplateUtils.renderTpl("controller.ftl", tableInfo);
        }

    }
}