package fun.junjie.autotools.config.project;

import fun.junjie.autotools.domain.config.TplConfig;
import lombok.Data;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ProjectConfig {


    public static final String PROJECT_CONFIG_DIR =
            "D:\\Download\\spring-demo-master\\spring-demo-master\\cn\\AutoTools\\src\\main\\resources\\config2";

    private static ProjectConfig INSTANCE;

    private String configFile;
    private String projectName;
    private String tablePrefix;

    private Map<String, TplConfig> tplName2TplConfig;

    private ProjectConfig() {
    }


    public static TplConfig getTplConfig(String tplFile) {
        if (INSTANCE.tplName2TplConfig.containsKey(tplFile)) {
            throw new RuntimeException("Get Tpl Config Wrong.");
        }
        return INSTANCE.tplName2TplConfig.get(tplFile);
    }

    public static String getTablePrefix() {
        return INSTANCE.tablePrefix;
    }

    public static void init(String configFile) {

        if (INSTANCE != null) {
            throw new RuntimeException("Project Config Has Benn Init.");
        }

        INSTANCE = new ProjectConfig();

        INSTANCE.configFile = configFile;

        Path configFilePath = Paths.get(PROJECT_CONFIG_DIR, configFile);

        if (!Files.exists(configFilePath) || Files.isDirectory(configFilePath)) {
            throw new RuntimeException("Config File Init Wrong.");
        }

        try {
            Yaml yaml = new Yaml();

            ProjectConfInternal projectConfInternal =
                    yaml.loadAs(new FileReader(configFilePath.toString()), ProjectConfInternal.class);

            INSTANCE.projectName = projectConfInternal.getProjectName();
            INSTANCE.tablePrefix = projectConfInternal.getTblPrefix();
            INSTANCE.tplName2TplConfig = new HashMap<>();

            for (TplConfInternal tplConfInternal : projectConfInternal.getTplConfig()) {
                INSTANCE.tplName2TplConfig.put(tplConfInternal.getTplFile(), new TplConfig(
                        tplConfInternal.getTplFile(),
                        projectConfInternal.getTplRoot(),
                        tplConfInternal.getOutputDir()));
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Config File Init Wrong.");
        }
    }


    @Data
    @SuppressWarnings("WeakerAccess")
    public static class ProjectConfInternal {
        private String projectName;
        private String tblPrefix;
        private String tplRoot;
        private List<TplConfInternal> tplConfig;
    }

    @Data
    @SuppressWarnings("WeakerAccess")
    public static class TplConfInternal {
        private String tplFile;
        private String outputDir;
    }

    public static void main(String[] args) {
        ProjectConfig.init("project_dyf.yml");
        System.out.println("");
    }

}
