package fun.junjie.autotools.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import fun.junjie.autotools.constant.ToolsConfig;
import fun.junjie.autotools.domain.yaml.TableRoot;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class YamlUtils {
    public static void dumpObject(TableRoot sourceObj, Path path) {
        // 将原始的对象序列化成json（字段按照原对象的声明顺序）
        JSON.DEFAULT_GENERATE_FEATURE &= ~SerializerFeature.SortField.getMask();
        SerializeConfig serializeConfig = new SerializeConfig(true);
        serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        String jsonWithFieldOrder = JSON.toJSONString(sourceObj, serializeConfig);

        try {
            // 将jsonWithFieldOrder转换成Map，并输出到yaml文件
            FileWriter fileWriter = new FileWriter(path.toString());
            Yaml yaml = new Yaml();
            fileWriter.write(yaml.dumpAsMap(JSON.parseObject(jsonWithFieldOrder, LinkedHashMap.class, Feature.OrderedField)));
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("Dump Wrong...");
        }
    }

    public static List<TableRoot> loadObject() {

        File outputDir = new File(ToolsConfig.YAML_OUTPUT_DIR);
        if (!outputDir.isDirectory()) {
            throw new RuntimeException("Load Wrong...");
        }

        File[] yamlFiles = outputDir.listFiles();
        if (yamlFiles == null) {
            throw new RuntimeException("Load Wrong...");
        }

        try {
            List<TableRoot> result = new ArrayList<>();

            for (File file : yamlFiles) {
                String jsonMiddle = JSON.toJSONString(new Yaml().load(new FileReader(file)));
                TableRoot tableRoot = JSONObject.parseObject(jsonMiddle, TableRoot.class);
                result.add(tableRoot);
            }

            return result;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Load Wrong...");
        }


    }
}
