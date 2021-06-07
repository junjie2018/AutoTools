//package fun.junjie.autotools.config.project;
//
//import fun.junjie.autotools.domain.config.TplConfig;
//import lombok.Data;
//import org.yaml.snakeyaml.Yaml;
//
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.*;
//
//@Data
//public class ProjectConfig {
//
//
//    public static final String PROJECT_CONFIG_DIR =
//            "D:\\Download\\spring-demo-master\\spring-demo-master\\cn\\AutoTools\\src\\main\\resources\\config";
//
//    private static ProjectConfig INSTANCE;
//
//    private String configFile;
//    private String projectName;
//    private String tablePrefix;
//    private Map<String, TblGenerate> tableToGenerates;
//    private Map<String, TplConfig> tplName2TplConfig;
//
//    private ProjectConfig() {
//    }
//
//
//    public static TplConfig getTplConfig(String tplFile) {
//        if (!INSTANCE.tplName2TplConfig.containsKey(tplFile)) {
//            throw new RuntimeException("Get Tpl Config Wrong.");
//        }
//        return INSTANCE.tplName2TplConfig.get(tplFile);
//    }
//
//    public static String getTablePrefix() {
//        return INSTANCE.tablePrefix;
//    }
//
//    public static String getProjectName() {
//        return INSTANCE.projectName;
//    }
//
//    public static String getEntityName(String tableName) {
//        TblGenerate tblGenerate = INSTANCE.tableToGenerates.get(tableName);
//        if (tblGenerate == null) {
//            return "";
//        } else {
//            return tblGenerate.getEntityName();
//        }
//    }
//
//    public static boolean needGenerate(String tableName) {
//        if (INSTANCE.tableToGenerates.size() == 0) {
//            return true;
//        }
//
//        return INSTANCE.tableToGenerates.containsKey(tableName);
//    }
//
//    public static boolean isPrimaryKey(String tableName, String fieldName) {
//        return "id".equals(fieldName);
//    }
//
//    public static void init(String configFile) {
//
//        if (INSTANCE != null) {
//            throw new RuntimeException("Project Config Has Benn Init.");
//        }
//
//        INSTANCE = new ProjectConfig();
//
//        INSTANCE.configFile = configFile;
//
//
//        // 加载所有的模板文件
//
//
//        Path configFilePath = Paths.get(PROJECT_CONFIG_DIR, configFile);
//
//        if (!Files.exists(configFilePath) || Files.isDirectory(configFilePath)) {
//            throw new RuntimeException("Config File Init Wrong.");
//        }
//
//        try {
//            Yaml yaml = new Yaml();
//
//            ProjectConfInternal projectConfInternal =
//                    yaml.loadAs(new FileReader(configFilePath.toString()), ProjectConfInternal.class);
//
//            INSTANCE.projectName = projectConfInternal.getProjectName();
//            INSTANCE.tablePrefix = projectConfInternal.getTblPrefix();
//            INSTANCE.tplName2TplConfig = new HashMap<>();
//            INSTANCE.tableToGenerates = new HashMap<>();
//
//            if (projectConfInternal.getTblGenerates() != null) {
//                for (TblGenerate tblGenerate : projectConfInternal.getTblGenerates()) {
//                    INSTANCE.tableToGenerates.put(tblGenerate.getTblName(), tblGenerate);
//                }
//            }
//
//            if (projectConfInternal.getTplConfig() != null) {
//                for (TplConfInternal tplConfInternal : projectConfInternal.getTplConfig()) {
//                    INSTANCE.tplName2TplConfig.put(tplConfInternal.getTplFile(), new TplConfig(
//                            tplConfInternal.getTplFile(),
//                            projectConfInternal.getTplRoot(),
//                            tplConfInternal.getOutputDir(),
//                            tplConfInternal.ignoreField == null ? new ArrayList<>() : tplConfInternal.ignoreField,
//                            tplConfInternal.strategy == null ? "null" : tplConfInternal.strategy));
//                }
//            }
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException("Config File Init Wrong.");
//        }
//    }
//
//
//    @Data
//    @SuppressWarnings("WeakerAccess")
//    public static class ProjectConfInternal {
//        private String projectName;
//        private String tblPrefix;
//        private String tplRoot;
//        private List<TblGenerate> tblGenerates;
//        private List<TplConfInternal> tplConfig;
//    }
//
//    @Data
//    @SuppressWarnings("WeakerAccess")
//    public static class TplConfInternal {
//        private String tplFile;
//        private String outputDir;
//        private List<String> ignoreField;
//        private String strategy;
//    }
//
//    @Data
//    public static class TblGenerate {
//        private String tblName;
//        private String entityName;
//    }
//
//    public static void main(String[] args) {
//        ProjectConfig.init("project_dyf.yml");
//        System.out.println("");
//    }
//
//}
