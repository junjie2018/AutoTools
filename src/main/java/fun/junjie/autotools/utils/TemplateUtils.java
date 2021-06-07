package fun.junjie.autotools.utils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import fun.junjie.autotools.config.ProjectConfig;
import fun.junjie.autotools.config.tools.GenerateStrategy;
import fun.junjie.autotools.config.tools.TemplatesConfig;
import fun.junjie.autotools.config.tools.ToolsConfig;
import fun.junjie.autotools.domain.java.TableInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateUtils {

    private final ProjectConfig projectConfig;
    private final ToolsConfig toolsConfig;

    private static Path curTemplateDir;
    public static Map<String, String> tplFileNameToRelativeNameMap;
    public static Map<String, TemplatesConfig> tplFileNameToTemplatesConfig;

    @PostConstruct
    public void init() {

        tplFileNameToRelativeNameMap = new HashMap<>(20);
        tplFileNameToTemplatesConfig = new HashMap<>(20);

        curTemplateDir = Paths.get(projectConfig.getTemplateDir(), toolsConfig.getProjectName());

        // 填充tplFileNameToRelativeNameMap
        walkThrough(curTemplateDir, file -> {

            Path tplAbsolutePath = file.toAbsolutePath();

            Path tplRelativizePath = curTemplateDir.relativize(tplAbsolutePath);

            if (tplFileNameToRelativeNameMap.containsKey(file.getFileName().toString())) {
                throw new RuntimeException("Wrong When Init tplFileNameToRelativeNameMap");
            }
            tplFileNameToRelativeNameMap.put(file.getFileName().toString(), tplRelativizePath.toString());
        });

        // 填充tplFileNameToTemplatesConfig
        for (TemplatesConfig templatesConfig : toolsConfig.getTemplatesConfig()) {
            if (tplFileNameToTemplatesConfig.containsKey(templatesConfig.getTemplateFilename().trim())) {
                throw new RuntimeException("Wrong When Init tplFileNameToTemplatesConfig");
            }
            tplFileNameToTemplatesConfig.put(templatesConfig.getTemplateFilename().trim(), templatesConfig);
        }
    }


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
            e.printStackTrace();
            throw new RuntimeException("Wrong");
        }
    }

    public static void renderTpl(String tplFileName, String outputFileName, TableInfo tableInfo) {
        TemplatesConfig templatesConfig = tplFileNameToTemplatesConfig.get(tplFileName);
        if (templatesConfig == null) {
            throw new RuntimeException("No Config For " + tplFileName);
        }


        // 如果是只在初次运行时生成的话，需要判断目标文件是否存在，如果存在则不生成
        if (templatesConfig.getGenerateStrategy() != null
                && templatesConfig.getGenerateStrategy() == GenerateStrategy.ONLY_FIRST) {
            if (Files.exists(Paths.get(templatesConfig.getOutputPath(), outputFileName))) {
                return;
            }
        }

        TableInfo tableInfoToRender = ObjectUtils.deepCopy(tableInfo, TableInfo.class);

        // 如果用户设置了忽略的字段，该处用于剔除需要剔除的功能
        if (templatesConfig.getIgnoreFields() != null
                && templatesConfig.getIgnoreFields().size() > 0) {
            List<TableInfo.Field> newFields = new ArrayList<>();

            for (TableInfo.Field entityField : tableInfoToRender.getEntityFields()) {
                if (!templatesConfig.getIgnoreFields().contains(entityField.getFieldName())) {
                    newFields.add(entityField);
                }
            }

            tableInfoToRender.setEntityFields(newFields);
        }

        renderTpl(tplFileName, outputFileName, templatesConfig, Collections.singletonMap("tableInfo", tableInfoToRender));
    }

    public static void renderTpl(String tplFileName, String outputFileName, TableInfo.EnumClass enumClass) {
        TemplatesConfig templatesConfig = tplFileNameToTemplatesConfig.get(tplFileName);
        if (templatesConfig == null) {
            throw new RuntimeException("No Config For " + tplFileName);
        }

        renderTpl(tplFileName, outputFileName, templatesConfig, Collections.singletonMap("enumClass", enumClass));
    }

    private static void renderTpl(String templateFileName, String outputFileName,
                                  TemplatesConfig templatesConfig,
                                  Map<String, Object> renderData) {

        try {

            Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
            configuration.setDirectoryForTemplateLoading(curTemplateDir.toFile());
            configuration.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_31).build());

            // 如果目录不存在，则创建该目录
            if (!Files.exists(Paths.get(templatesConfig.getOutputPath()))) {
                Files.createDirectories(Paths.get(templatesConfig.getOutputPath()));
            }

            Path outputPath = Paths.get(templatesConfig.getOutputPath(), outputFileName);
            FileWriter fileWriter = new FileWriter(outputPath.toString());

            Template template = configuration.getTemplate(tplFileNameToRelativeNameMap.get(templateFileName));
            template.process(renderData, fileWriter);

            fileWriter.flush();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Render Template Wrong.");
        }
    }
}
