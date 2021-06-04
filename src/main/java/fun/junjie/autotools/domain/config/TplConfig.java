package fun.junjie.autotools.domain.config;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TplConfig {
    private String tplFile;
    private String tplRoot;
    private String outputDir;
    private List<String> ignoreField;
    private String strategy;
}
