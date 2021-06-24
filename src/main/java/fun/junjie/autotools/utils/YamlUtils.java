//package fun.junjie.autotools.utils;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.PropertyNamingStrategy;
//import com.alibaba.fastjson.serializer.SerializeConfig;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import fun.junjie.autotools.config.ProjectConfig;
//import fun.junjie.autotools.config.ToolsConfig;
//import fun.junjie.autotools.domain.TableInfo;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.yaml.snakeyaml.Yaml;
//
//import javax.annotation.PostConstruct;
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//@RequiredArgsConstructor
//public class YamlUtils {
//
//    private final ToolsConfig toolsConfig;
//    private final ProjectConfig projectConfig;
//    private static ToolsConfig toolsConfigStatic;
//    private static ProjectConfig projectConfigStatic;
//
//    @PostConstruct
//    public void init() {
//        toolsConfigStatic = toolsConfig;
//        projectConfigStatic = projectConfig;
//    }
//
//
//    /**
//     * 将对象序列化成Yaml文件，序列化时保持字段顺序与声明一致
//     */
//    public static void dumpObject(List<TableInfo> sourceObj, Path dirPath, String fileName) {
//
//        try {
//
//            if (!Files.exists(dirPath)) {
//                Files.createDirectory(dirPath);
//            }
//
//            // 将原始的对象序列化成json（字段按照原对象的声明顺序）
//            JSON.DEFAULT_GENERATE_FEATURE &= ~SerializerFeature.SortField.getMask();
//            SerializeConfig serializeConfig = new SerializeConfig(true);
//            serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
//            String jsonWithFieldOrder = JSON.toJSONString(sourceObj, serializeConfig);
//
//            // 将jsonWithFieldOrder转换成Map，并输出到yaml文件
//            FileWriter fileWriter = new FileWriter(Paths.get(dirPath.toString(), fileName).toString());
//            Yaml yaml = new Yaml();
////            fileWriter.write(yaml.dumpAsMap(JSON.parseObject(jsonWithFieldOrder, LinkedHashMap.class, Feature.OrderedField)));
////            fileWriter.write(yaml.dumpAsMap(JSON.parseArray(jsonWithFieldOrder, LinkedHashMap.class)));
//            fileWriter.write(yaml.dumpAll(sourceObj.iterator()));
//            fileWriter.close();
//        } catch (IOException e) {
//            throw new RuntimeException("Dump Wrong...");
//        }
//    }
//
//    /**
//     * 将多个Yaml文件反序列化成对象列表
//     */
//    public static List<TableInfo> loadObject() {
//
//        Path tableInfoDir = Paths.get(projectConfigStatic.getTableInfoDir(), toolsConfigStatic.getProjectName());
//        if (!Files.exists(tableInfoDir) || !Files.isDirectory(tableInfoDir)) {
//            throw new RuntimeException("Load Wrong...");
//        }
//
//        try {
//            List<Path> yamlFiles = Files.list(tableInfoDir).collect(Collectors.toList());
//
//            for (Path yamlFile : yamlFiles) {
//                String jsonMiddle = JSON.toJSONString(new Yaml().load(new FileReader(yamlFile.toFile())));
//                return JSONObject.parseArray(jsonMiddle, TableInfo.class);
//            }
//
//            return null;
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Load Wrong...");
//        }
//    }
//}
