//package fun.junjie.autotools.utils;
//
//import fun.junjie.autotools.config.project.ProjectConfig;
//import fun.junjie.autotools.constant.ToolsConfig;
//
//import java.nio.file.Paths;
//
//public class ProcessUtils {
//    public static void compareTwoDirs() {
//        try {
//            Runtime runtime = Runtime.getRuntime();
//            runtime.exec(String.format("%s %s %s",
//                    ToolsConfig.COMPARE_EXE_PATH,
//                    Paths.get(ToolsConfig.TEMP_DIR, ProjectConfig.getProjectName()).toString(),
//                    Paths.get(ToolsConfig.TABLES_INFO_DIR, ProjectConfig.getProjectName()).toString()));
//        } catch (Exception e) {
//            System.out.println("open failure");
//        }
//    }
//}
