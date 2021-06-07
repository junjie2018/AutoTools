package fun.junjie.autotools.config.tools;

import lombok.Data;

import java.util.List;

@Data
public class TemplatesConfig {
    private String templateFilename;
    private String outputPath;
    private GenerateStrategy generateStrategy;
    private List<String> ignoreFields;
}
