package fun.junjie.autotools.service;

import fun.junjie.autotools.domain.postgre.Table;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class TableServiceTest {

    @Autowired
    private TableService tableService;

    @Test
    void test() {
        List<Table> tables = tableService.getTables();
        System.out.println("");
    }
}
