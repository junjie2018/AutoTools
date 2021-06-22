package fun.junjie.autotools.config.tools;

import lombok.Data;

import java.util.List;

@Data
public class TemplatesConfig {
    private String templateFilename;
    private String outputPath;
    private String outputFilename;
    private String filePackage;
    private List<String> packagesToImport;
    private GenerateStrategy generateStrategy;
    private List<String> ignoreFields;
}
