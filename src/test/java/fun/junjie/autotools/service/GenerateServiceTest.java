package fun.junjie.autotools.service;

import fun.junjie.autotools.domain.TableInfo;
import fun.junjie.autotools.domain.postgre.Table;
import fun.junjie.autotools.utils.TemplateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GenerateServiceTest {

    @Autowired
    private GenerateService generateService;
    @Autowired
    private TableService tableService;

    @Test
    void getTableInfoFromTable() {

        List<Table> tables = tableService.getTables();

        List<TableInfo> tableInfos = generateService.getTableInfoFromTable(tables);

        TemplateUtils.renderTpl("entity.ftl", "tmp.java", tableInfos.get(0));

        System.out.println("");
    }
}