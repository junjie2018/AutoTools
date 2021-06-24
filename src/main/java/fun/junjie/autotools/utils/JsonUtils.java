package fun.junjie.autotools.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import fun.junjie.autotools.domain.TableInfo;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class JsonUtils {

    public static void dumpObject(Object object, Path dirPath, String fileName) {
        try (FileWriter fileWriter = new FileWriter(Paths.get(dirPath.toString(), fileName).toString())) {
            JSON.DEFAULT_GENERATE_FEATURE &= ~SerializerFeature.SortField.getMask();
            SerializeConfig serializeConfig = new SerializeConfig(true);
            serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;

            String jsonStr = JSON.toJSONString(object, serializeConfig, SerializerFeature.PrettyFormat);

            fileWriter.write(jsonStr);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Dump Object Wrong");
        }
    }

    public static List<TableInfo> loadObject(Path dirPath, String fileName) {
        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(Paths.get(dirPath.toString(), fileName).toString()))) {

            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            String s = sb.toString();

            return JSON.parseArray(sb.toString(), TableInfo.class);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Load Object Wrong");
        }
    }
}
