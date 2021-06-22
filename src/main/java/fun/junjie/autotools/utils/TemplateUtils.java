package fun.junjie.autotools.utils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import fun.junjie.autotools.config.ProjectConfig;
import fun.junjie.autotools.config.tools.GenerateStrategy;
import fun.junjie.autotools.config.tools.TemplatesConfig;
import fun.junjie.autotools.config.tools.ToolsConfig;
import fun.junjie.autotools.domain.ColumnInfo;
import fun.junjie.autotools.domain.TableInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.*;
import java.util.*;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("Duplicates")
public class TemplateUtils {

    private final ProjectConfig projectConfig;
    private final ToolsConfig toolsConfig;

    private static Path curTemplateDir;
    private static Map<String, String> tplFileNameToRelativeNameMap;
    private static Map<String, TemplatesConfig> tplFileNameToTemplatesConfig;

    @PostConstruct
    public void init() {

        tplFileNameToRelativeNameMap = new HashMap<>(20);
        tplFileNameToTemplatesConfig = new HashMap<>(20);

        curTemplateDir = Paths.get(projectConfig.getTemplateDir());
        Path curPathTemplateDir = Paths.get(projectConfig.getTemplateDir(), toolsConfig.getProjectName());
        Path fragmentsTemplateDir = Paths.get(projectConfig.getTemplateDir(), "fragments");

        // 填充tplFileNameToRelativeNameMap
        FileUtils.walkThrough(curPathTemplateDir, file -> {

            Path tplAbsolutePath = file.toAbsolutePath();

            Path tplRelativizePath = curTemplateDir.relativize(tplAbsolutePath);

            if (tplFileNameToRelativeNameMap.containsKey(file.getFileName().toString())) {
                throw new RuntimeException("Wrong When Init tplFileNameToRelativeNameMap");
            }
            tplFileNameToRelativeNameMap.put(file.getFileName().toString(), tplRelativizePath.toString());
        });


        // 填充tplFileNameToRelativeNameMap
        FileUtils.walkThrough(fragmentsTemplateDir, file -> {

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

    public static void renderTpl(String tplFileName, TableInfo tableInfo) {
        TemplatesConfig templatesConfig = tplFileNameToTemplatesConfig.get(tplFileName);
        renderTpl(tplFileName,
                String.format(templatesConfig.getOutputFilename(), tableInfo.getEntityClassName()),
                tableInfo);
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
            List<ColumnInfo> columnInfosNew = new ArrayList<>();

            for (ColumnInfo columnInfo : tableInfoToRender.getColumnInfos()) {
                if (templatesConfig.getIgnoreFields().contains(columnInfo.getColumnName())) {
                    columnInfosNew.add(columnInfo);
                }
            }

            tableInfoToRender.setColumnInfos(columnInfosNew);
        }

        renderTpl(tplFileName, outputFileName, templatesConfig, Collections.singletonMap("tableInfo", tableInfoToRender));
    }

//    public static void renderTpl(String tplFileName, String outputFileName, TableInfo.EnumClass enumClass) {
//        TemplatesConfig templatesConfig = tplFileNameToTemplatesConfig.get(tplFileName);
//        if (templatesConfig == null) {
//            throw new RuntimeException("No Config For " + tplFileName);
//        }
//
//        renderTpl(tplFileName, outputFileName, templatesConfig, Collections.singletonMap("enumClass", enumClass));
//    }

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

            // 在renderData中添加templateConfig信息
            Map<String, Object> renderDataNew = new HashMap<>(renderData);
            renderDataNew.put("templateConfig", tplFileNameToTemplatesConfig.get(templateFileName));

            template.process(renderDataNew, fileWriter);

            fileWriter.flush();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Render Template Wrong.");
        }
    }

    public static void renderTplString(String tplFileName, TableInfo tableInfo) {
        TemplatesConfig templatesConfig = tplFileNameToTemplatesConfig.get(tplFileName);

        TableInfo tableInfoToRender = ObjectUtils.deepCopy(tableInfo, TableInfo.class);

        // 如果用户设置了忽略的字段，该处用于剔除需要剔除的功能
        if (templatesConfig != null
                && templatesConfig.getIgnoreFields() != null
                && templatesConfig.getIgnoreFields().size() > 0) {
            List<ColumnInfo> columnInfosNew = new ArrayList<>();

            for (ColumnInfo columnInfo : tableInfoToRender.getColumnInfos()) {
                if (templatesConfig.getIgnoreFields().contains(columnInfo.getColumnName())) {
                    columnInfosNew.add(columnInfo);
                }
            }

            tableInfoToRender.setColumnInfos(columnInfosNew);
        }

        renderTplString(tplFileName, templatesConfig, Collections.singletonMap("tableInfo", tableInfoToRender));
    }

    private static void renderTplString(String templateFileName,
                                        TemplatesConfig templatesConfig,
                                        Map<String, Object> renderData) {

        try {

            Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
            configuration.setDirectoryForTemplateLoading(curTemplateDir.toFile());
            configuration.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_31).build());

            StringWriter stringWriter = new StringWriter();

            Template template = configuration.getTemplate(tplFileNameToRelativeNameMap.get(templateFileName));

            // 在renderData中添加templateConfig信息
            Map<String, Object> renderDataNew = new HashMap<>(renderData);
            renderDataNew.put("templateConfig", tplFileNameToTemplatesConfig.get(templateFileName));

            template.process(renderDataNew, stringWriter);

            stringWriter.flush();

            System.out.println(stringWriter.toString());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Render Template Wrong.");
        }

    }
}
