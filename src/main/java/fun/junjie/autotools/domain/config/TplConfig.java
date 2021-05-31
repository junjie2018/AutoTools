package fun.junjie.autotools.domain.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TplConfig {
    private String tplFile;
    private String tplRoot;
    private String outputDir;
}
