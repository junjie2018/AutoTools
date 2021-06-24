package fun.junjie.autotools.config.tools;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class TemplateConfig {
    private String templateFilename;
    private String outputPath;
    private String outputFilename;
    private String filePackage;
    private List<String> packagesToImport;
    private GenerateStrategy generateStrategy;
    private List<String> ignoreFields;

    private Map<String, String> otherConfigs;

    public List<String> getPackagesToImport() {
        return packagesToImport == null ? Collections.emptyList() : packagesToImport;
    }
}
