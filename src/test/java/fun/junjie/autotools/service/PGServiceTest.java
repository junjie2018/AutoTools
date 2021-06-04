package fun.junjie.autotools.service;

import fun.junjie.autotools.config.project.ProjectConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PGServiceTest {

    @Autowired
    private PGService pgService;

    @Test
    void generateDBYaml() {

        ProjectConfig.init("project_auth.yml");

        pgService.generateYaml();

//        ProcessUtils.compareTwoDirs();

    }

    @Test
    void generateJavaCode() {

        ProjectConfig.init("project_auth.yml");

        pgService.generateJavaCode();

    }


}