package fun.junjie.autotools.utils;

import fun.junjie.autotools.config.ProjectConfig;
import fun.junjie.autotools.config.TemplateConfig;
import fun.junjie.autotools.config.GeneratorConfig;
import fun.junjie.autotools.domain.EnumInfo;
import fun.junjie.autotools.domain.TableInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.file.*;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("Duplicates")
public class TemplateUtils {

    private final ProjectConfig projectConfig;
    private final GeneratorConfig generatorConfig;

    private static Map<String, String> tplFileNameToRelativeNameMap;
    private static Map<String, TemplateConfig> tplFileNameToTemplatesConfig;

    @PostConstruct
    public void init() {

        tplFileNameToRelativeNameMap = new HashMap<>(20);
        tplFileNameToTemplatesConfig = new HashMap<>(20);

        Path templateDir = Paths.get(projectConfig.getTemplateDir());

        // 填充tplFileNameToRelativeNameMap
        FileUtils.walkThrough(templateDir, file -> {

            Path tplAbsolutePath = file.toAbsolutePath();

            Path tplRelativizePath = templateDir.relativize(tplAbsolutePath);

//            if (tplFileNameToRelativeNameMap.containsKey(file.getFileName().toString())) {
//                throw new RuntimeException("Wrong When Init tplFileNameToRelativeNameMap");
//            }

            tplFileNameToRelativeNameMap.put(file.getFileName().toString(), tplRelativizePath.toString());
        });

        // 填充tplFileNameToTemplatesConfig
        for (TemplateConfig templateConfig : generatorConfig.getTemplateConfigs()) {
            if (tplFileNameToTemplatesConfig.containsKey(templateConfig.getTemplateFilename().trim())) {
                throw new RuntimeException("Wrong When Init tplFileNameToTemplatesConfig");
            }
            tplFileNameToTemplatesConfig.put(templateConfig.getTemplateFilename().trim(), templateConfig);
        }
    }



    public static void renderTpl(String tplFileName, TableInfo tableInfo) {
//        TemplateConfig templateConfig = tplFileNameToTemplatesConfig.get(tplFileName);
//        renderTpl(tplFileName,
//                String.format(templateConfig.getOutputFilename(), tableInfo.getbeanClass()),
//                tableInfo);
    }

    public static void renderTpl(String tplFileName, String outputFileName, TableInfo tableInfo) {
//        TemplateConfig templateConfig = tplFileNameToTemplatesConfig.get(tplFileName);
//        if (templateConfig == null) {
//            throw new RuntimeException("No Config For " + tplFileName);
//        }
//
//        // 如果是只在初次运行时生成的话，需要判断目标文件是否存在，如果存在则不生成
//        if (templateConfig.getGenerateStrategy() != null
//                && templateConfig.getGenerateStrategy() == GenerateStrategy.ONLY_FIRST) {
//            if (Files.exists(Paths.get(templateConfig.getOutputPath(), outputFileName))) {
//                return;
//            }
//        }
//
//        TableInfo tableInfoToRender = ObjectUtils.deepCopy(tableInfo, TableInfo.class);
//
//        // 如果用户设置了忽略的字段，该处用于剔除需要剔除的功能
//        if (templateConfig.getIgnoreFields() != null
//                && templateConfig.getIgnoreFields().size() > 0) {
//            List<ColumnInfo> columnInfosNew = new ArrayList<>();
//
//            for (ColumnInfo columnInfo : tableInfoToRender.getColumnInfos()) {
//                if (!templateConfig.getIgnoreFields().contains(columnInfo.getColumnName())) {
//                    columnInfosNew.add(columnInfo);
//                }
//            }
//
//            tableInfoToRender.setColumnInfos(columnInfosNew);
//        }
//
//        renderTpl(tplFileName, outputFileName, templateConfig, Collections.singletonMap("tableInfo", tableInfoToRender));
    }

    public static void renderTpl(String tplFileName, EnumInfo enumInfo) {
//        TemplateConfig templateConfig = tplFileNameToTemplatesConfig.get(tplFileName);
//        if (templateConfig == null) {
//            throw new RuntimeException("No Config For " + tplFileName);
//        }
//
//        renderTpl(tplFileName,
//                String.format(templateConfig.getOutputFilename(), enumInfo.getEnumClassName()),
//                enumInfo);
//    }
//
//    public static void renderTpl(String tplFileName, String outputFileName, EnumInfo enumInfo) {
//        TemplateConfig templateConfig = tplFileNameToTemplatesConfig.get(tplFileName);
//        if (templateConfig == null) {
//            throw new RuntimeException("No Config For " + tplFileName);
//        }
//
//        renderTpl(tplFileName, outputFileName, templateConfig, Collections.singletonMap("enumInfo", enumInfo));
    }

    private static void renderTpl(String templateFileName, String outputFileName,
                                  TemplateConfig templateConfig,
                                  Map<String, Object> renderData) {

//        try {
//
//            Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
//            configuration.setDirectoryForTemplateLoading(curTemplateDir.toFile());
//            configuration.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_31).build());
//            configuration.setSharedVariable("fragment", new FragmentDirective());
//            configuration.setSharedVariable("include", new IncludeDirective());
//
//            // 如果目录不存在，则创建该目录
//            if (!Files.exists(Paths.get(templateConfig.getOutputPath()))) {
//                Files.createDirectories(Paths.get(templateConfig.getOutputPath()));
//            }
//
//            Path outputPath = Paths.get(templateConfig.getOutputPath(), outputFileName);
//            FileWriter fileWriter = new FileWriter(outputPath.toString());
//
//            Template template = configuration.getTemplate(tplFileNameToRelativeNameMap.get(templateFileName));
//
//            // 在renderData中添加templateConfig信息
//            Map<String, Object> renderDataNew = new HashMap<>(renderData);
//            renderDataNew.put("templateConfig", tplFileNameToTemplatesConfig.get(templateFileName));
//
//            template.process(renderDataNew, fileWriter);
//
//            fileWriter.flush();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Render Template Wrong.");
//        }
    }

    public static void renderTplString(String tplFileName, TableInfo tableInfo) {
//        TemplateConfig templateConfig = tplFileNameToTemplatesConfig.get(tplFileName);
//
//        TableInfo tableInfoToRender = ObjectUtils.deepCopy(tableInfo, TableInfo.class);
//
//        // 如果用户设置了忽略的字段，该处用于剔除需要剔除的功能
//        if (templateConfig != null
//                && templateConfig.getIgnoreFields() != null
//                && templateConfig.getIgnoreFields().size() > 0) {
//            List<ColumnInfo> columnInfosNew = new ArrayList<>();
//
//            for (ColumnInfo columnInfo : tableInfoToRender.getColumnInfos()) {
//                if (templateConfig.getIgnoreFields().contains(columnInfo.getColumnName())) {
//                    columnInfosNew.add(columnInfo);
//                }
//            }
//
//            tableInfoToRender.setColumnInfos(columnInfosNew);
//        }
//
//        renderTplString(tplFileName, templateConfig, Collections.singletonMap("tableInfo", tableInfoToRender));
    }

    private static void renderTplString(String templateFileName,
                                        TemplateConfig templateConfig,
                                        Map<String, Object> renderData) {

//        try {
//
//            Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
//            configuration.setDirectoryForTemplateLoading(curTemplateDir.toFile());
//            configuration.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_31).build());
//
//            StringWriter stringWriter = new StringWriter();
//
//            Template template = configuration.getTemplate(tplFileNameToRelativeNameMap.get(templateFileName));
//
//            // 在renderData中添加templateConfig信息
//            Map<String, Object> renderDataNew = new HashMap<>(renderData);
//            renderDataNew.put("templateConfig", tplFileNameToTemplatesConfig.get(templateFileName));
//
//            template.process(renderDataNew, stringWriter);
//
//            stringWriter.flush();
//
//            System.out.println(stringWriter.toString());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Render Template Wrong.");
//        }
    }
}
