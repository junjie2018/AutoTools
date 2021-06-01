package fun.junjie.autotools.service;

import fun.junjie.autotools.utils.ProcessUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PGServiceTest {

    @Autowired
    private PGService pgService;

    @Test
    void generateDBYaml() {

        pgService.generateYaml();

        ProcessUtils.compareTwoDirs();

    }


}