package fun.junjie.autotools.service;

import fun.junjie.autotools.config.ProjectConfig;
import fun.junjie.autotools.config.tools.ToolsConfig;
import fun.junjie.autotools.utils.TemplateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
class PGServiceTest {

    @Autowired
    private PGService pgService;

    @Test
    void generateDBYaml() {


        pgService.generateYaml();

    }

    @Test
    void generateJavaCode() {

        pgService.generateJavaCode();

    }

    @Autowired
    private ToolsConfig toolsConfig;
    @Autowired
    private ProjectConfig projectConfig;

    @Test
    void tmp() {
        Map<String, String> tplFileNameToRelativeNameMap = TemplateUtils.tplFileNameToRelativeNameMap;


        System.out.println("");
    }

}