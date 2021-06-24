package fun.junjie.autotools.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TemplateConfig {
    
    private String templateFilename;
    private String outputDirectory;
    private String outputFilename;
    private String currentPackage;
    private List<String> packageToImports;
    private List<String> ignoreFields;
    private GenerateStrategy generateStrategy;
    private Map<String, String> properties;

}
