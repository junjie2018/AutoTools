package fun.junjie.autotools.utils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import fun.junjie.autotools.config.project.ProjectConfig;
import fun.junjie.autotools.domain.config.TplConfig;
import fun.junjie.autotools.domain.java.TableInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class TemplateUtils {

    private static final String TEMPLATES_ROOT = "D:\\Download\\spring-demo-master\\spring-demo-master\\cn\\AutoTools\\src\\main\\resources\\templates\\dyf";

    private static void walkThrough(Path path, Consumer<Path> disposer) {
        if (path == null || !Files.exists(path) || !Files.isDirectory(path)) {
            throw new RuntimeException("Wrong");
        }

        try {
            Files.list(path).forEach(pathItem -> {
                if (Files.isDirectory(pathItem)) {
                    walkThrough(pathItem, disposer);
                } else {
                    disposer.accept(pathItem);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Wrong");
        }
    }

    public static void main(String[] args) {
        List<Path> paths = new ArrayList<>();

        walkThrough(Paths.get(TEMPLATES_ROOT), paths::add);

        for (Path path : paths) {
            System.out.println("- tmpFile: " + Paths.get(TEMPLATES_ROOT).relativize(path));
            System.out.println("  outputDir: ");
        }
    }


    public static void renderTpl(String tplFile, String fileName, TableInfo tableInfo) {

        TplConfig tplConfig = ProjectConfig.getTplConfig(tplFile);
        if ("first".equals(tplConfig.getStrategy())) {
            // 如果已经生成过，就取消生成
            if (Files.exists(Paths.get(tplConfig.getOutputDir(), fileName))) {
                return;
            }
        }


        renderTpl(tplFile, fileName, tplConfig, Collections.singletonMap("tableInfo", tableInfo));
    }

    public static void renderTpl(String tplFile, String fileName, TableInfo.EnumClass enumClass) {

        TplConfig tplConfig = ProjectConfig.getTplConfig(tplFile);

        renderTpl(tplFile, fileName, tplConfig, Collections.singletonMap("enumClass", enumClass));
    }

    private static void renderTpl(String tplFile, String fileName, TplConfig tplConfig, Map<String, Object> renderData) {

        try {
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
            configuration.setDirectoryForTemplateLoading(new File(tplConfig.getTplRoot()));
            configuration.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_31).build());

            String outputFile = Paths.get(tplConfig.getOutputDir(), fileName).toString();
            FileWriter fileWriter = new FileWriter(outputFile);

            Template template = configuration.getTemplate(tplFile);
            template.process(renderData, fileWriter);

            fileWriter.flush();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Render Template Wrong.");
        }
    }
}
