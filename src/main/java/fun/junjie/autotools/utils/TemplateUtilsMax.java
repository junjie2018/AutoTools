package fun.junjie.autotools.utils;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import fun.junjie.autotools.config.ProjectConfig;
import fun.junjie.autotools.config.TableConfig;
import fun.junjie.autotools.config.TemplateConfig;
import fun.junjie.autotools.config.ToolsConfig;
import fun.junjie.autotools.directives.FragmentDirective;
import fun.junjie.autotools.directives.IncludeDirective;
import fun.junjie.autotools.directives.NoSpaceLineDiretive;
import fun.junjie.autotools.domain.EnumInfo;
import fun.junjie.autotools.domain.TableInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("Duplicates")
@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateUtilsMax {

    private final ApplicationContext applicationContext;

    private static Configuration configuration;
    private static ToolsConfig toolsConfig;
    private static ProjectConfig projectConfig;

    private static final String TPL = "tpl";
    private static final String FRAGMENT = "fragment";

    private static Pattern FRAGMENT_PATTERN = Pattern.compile(
            "<@fragment name=\"([A-Za-z0-9-_]+)\">(.*?)</@fragment>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static Map<String, String> tplContentsMap;

    public static void render(List<TableInfo> tableInfos) {
        for (TableConfig tableConfig : toolsConfig.getTableConfigs()) {
            outer:
            for (String templateToGenerate : tableConfig.getTemplateToGenerate()) {
                for (TableInfo tableInfo : tableInfos) {
                    if (tableInfo.getTableName().equals(tableConfig.getTableName())) {
                        renderTpl(templateToGenerate, tableInfo);
                        continue outer;
                    }
                }
                throw new RuntimeException("No Table Info");
            }

            for (TableInfo tableInfo : tableInfos) {
                for (EnumInfo enumInfo : tableInfo.getEnumInfos()) {
                    TemplateUtilsMax.renderTpl("Enum.ftl", enumInfo);
                }
            }
        }
    }

    public static void renderTpl(String tplName, TableInfo tableInfo) {
        if (!tplContentsMap.containsKey(tplName)) {
            throw new RuntimeException("No This Fragment Tpl");
        }

        try {
            Template template = configuration.getTemplate(tplName);
            TemplateConfig templateConfig = toolsConfig.getTemplateConfig(tplName);
            processTemplate(template, tableInfo, templateConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void renderTpl(String tplName, EnumInfo enumInfo) {
        if (!tplContentsMap.containsKey(tplName)) {
            throw new RuntimeException("No This Fragment Tpl");
        }

        try {
            Template template = configuration.getTemplate(tplName);
            TemplateConfig templateConfig = toolsConfig.getTemplateConfig(tplName);
            processTemplate(template, enumInfo, templateConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void renderTplStr(String tplName, TableInfo tableInfo) {
        if (!tplContentsMap.containsKey(tplName)) {
            throw new RuntimeException("No This Fragment Tpl");
        }

        try {
            Template template = configuration.getTemplate(tplName);
            TemplateConfig templateConfig = toolsConfig.getTemplateConfig(tplName);
            processTemplateStr(template, tableInfo, templateConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFragmentTplName(String fragmentTplName, String fragmentName) {
        return fragmentTplName + "#" + fragmentName;
    }


    private static void processTemplate(Template template,
                                        TableInfo tableInfo,
                                        TemplateConfig templateConfig) {
        try {
            Map<String, Object> renderData = initMap(tableInfo, templateConfig);

            String outputFileName = getOutputFileName(templateConfig.getOutputFilename(), renderData);
            Path outputFilePath = Paths.get(templateConfig.getOutputDirectory(), outputFileName);

            FileWriter fileWriter = new FileWriter(outputFilePath.toString());

            processTemplate(template, new CustomFileWriter(fileWriter), renderData);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void processTemplate(Template template,
                                        EnumInfo enumInfo,
                                        TemplateConfig templateConfig) {
        try {
            Map<String, Object> renderData = initMap(enumInfo, templateConfig);

            String outputFileName = getOutputFileName(templateConfig.getOutputFilename(), renderData);
            Path outputFilePath = Paths.get(templateConfig.getOutputDirectory(), outputFileName);

            FileWriter fileWriter = new FileWriter(outputFilePath.toString());

            processTemplate(template, new CustomFileWriter(fileWriter), renderData);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void processTemplateStr(Template template,
                                           TableInfo tableInfo,
                                           TemplateConfig templateConfig) {

        StringWriter stringWriter = new StringWriter();

        Map<String, Object> renderData = initMap(tableInfo, templateConfig);

        processTemplate(template, new CustomStringWriter(stringWriter), renderData);

        System.out.println(stringWriter.toString());
    }

    private static void processTemplate(Template template, Writer writer, Map<String, Object> renderData) {
        try {
            template.process(renderData, writer);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, Object> initMap(EnumInfo enumInfo, TemplateConfig templateConfig) {
        Map<String, Object> renderDataMap = new HashMap<>();

        // enumInfo
        renderDataMap.put("enumItems", enumInfo.getEnumItems());
        renderDataMap.put("enumClass", enumInfo.getEnumClass());
        renderDataMap.put("enumComment", enumInfo.getEnumComment());
        renderDataMap.put("enumObject", enumInfo.getEnumObject());
        renderDataMap.put("enumValueType", enumInfo.getEnumValueType());

        // templateConfig中的Properties优先级更高（在构建properties的时候，将配置文件中的下滑线转换为驼峰）
        Map<String, String> properties = new HashMap<>();
        if (toolsConfig.getProperties() != null) {
            for (Map.Entry<String, String> entry : toolsConfig.getProperties().entrySet()) {
                properties.put(JStringUtils.strikethroughToCamelUncapitalized(entry.getKey()),
                        entry.getValue());
            }
        }

        if (templateConfig.getProperties() != null) {
            for (Map.Entry<String, String> entry : templateConfig.getProperties().entrySet()) {
                properties.put(JStringUtils.strikethroughToCamelUncapitalized(entry.getKey()),
                        entry.getValue());
            }
        }

        renderDataMap.put("packagesToImport", templateConfig.getPackagesToImport());
        renderDataMap.put("properties", properties);

        return renderDataMap;
    }

    private static Map<String, Object> initMap(TableInfo tableInfo, TemplateConfig templateConfig) {
        Map<String, Object> renderDataMap = new HashMap<>();

        // tableInfo
        renderDataMap.put("tableName", tableInfo.getTableName());
        renderDataMap.put("tableComment", tableInfo.getTableComment());
        renderDataMap.put("entityName", tableInfo.getEntityName());
        renderDataMap.put("beanClass", tableInfo.getBeanClass());
        renderDataMap.put("beanObject", tableInfo.getBeanObject());
        renderDataMap.put("columnInfos", tableInfo.getColumnInfos());
        renderDataMap.put("internalClassInfos", tableInfo.getInternalClassInfos());

        // templateConfig

        // templateConfig中的Properties优先级更高（在构建properties的时候，将配置文件中的下滑线转换为驼峰）
        Map<String, String> properties = new HashMap<>();
        if (toolsConfig.getProperties() != null) {
            for (Map.Entry<String, String> entry : toolsConfig.getProperties().entrySet()) {
                properties.put(JStringUtils.strikethroughToCamelUncapitalized(entry.getKey()),
                        entry.getValue());
            }
        }
        if (templateConfig.getProperties() != null) {
            for (Map.Entry<String, String> entry : templateConfig.getProperties().entrySet()) {
                properties.put(JStringUtils.strikethroughToCamelUncapitalized(entry.getKey()),
                        entry.getValue());
            }
        }

        renderDataMap.put("packagesToImport", templateConfig.getPackagesToImport());
        renderDataMap.put("properties", properties);

        return renderDataMap;
    }

    private static String getOutputFileName(String outputFilePattern, Map<String, Object> renderData) {
        // 临时方案，以后会换成正则
        if (outputFilePattern.contains("${beanClass}")) {
            return outputFilePattern.replace("${beanClass}", (String) renderData.get("beanClass"));
        } else if (outputFilePattern.contains("${enumClass}")) {
            return outputFilePattern.replace("${enumClass}", (String) renderData.get("enumClass"));
        }
        return outputFilePattern;
    }

    @PostConstruct
    private void init() {

        projectConfig = applicationContext.getBean(ProjectConfig.class);
        toolsConfig = applicationContext.getBean(ToolsConfig.class);

        tplContentsMap = new HashMap<>();

        FileUtils.walkThrough(projectConfig.getTemplateDir(), (tplFilePath) -> {
            File tplFile = tplFilePath.toFile();

            if (tplContentsMap.containsKey(tplFile.getName())) {
                throw new RuntimeException("Wrong When Load Template Infos");
            }

            try (FileInputStream fis = new FileInputStream(tplFile)) {
                byte[] tplBytes = new byte[(int) tplFile.length()];
                //noinspection ResultOfMethodCallIgnored
                fis.read(tplBytes);
                String tplContent = new String(tplBytes);
                tplContentsMap.put(tplFile.getName(), StringUtils.strip(tplContent.trim()));
                loadFragmentContents(tplFile.getName(), tplContent);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();

        for (Map.Entry<String, String> entry : tplContentsMap.entrySet()) {
            stringTemplateLoader.putTemplate(entry.getKey(), entry.getValue());
        }

        configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_31).build());
        configuration.setSharedVariable("fragment", new FragmentDirective());
        configuration.setSharedVariable("include", new IncludeDirective());
        configuration.setSharedVariable("noSpaceLine", new NoSpaceLineDiretive());
        configuration.setTemplateLoader(stringTemplateLoader);
    }

    private void loadFragmentContents(String tplFileName, String tplContent) {
        Matcher fragmentMatcher = FRAGMENT_PATTERN.matcher(tplContent);
        while (fragmentMatcher.find()) {
            tplContentsMap.put(
                    getFragmentTplName(tplFileName, fragmentMatcher.group(1)),
                    StringUtils.strip(fragmentMatcher.group(2)));
        }
    }

    public static String changeSpaceLinesToOne(String inputStr) {
        try {
            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);
            BufferedReader br = new BufferedReader(new StringReader(inputStr));

            int spaceLineCount = 0;
            String line;
            while ((line = br.readLine()) != null) {
                if ("".equals(StringUtils.trim(line))) {
                    spaceLineCount++;
                } else {
                    if (spaceLineCount != 0) {
                        bw.newLine();
                    }
                    bw.write(line);
                    bw.newLine();
                    spaceLineCount = 0;
                }
            }
            bw.flush();
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static String changeSpaceLinesToNone(String inputStr) {
        try {
            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);
            BufferedReader br = new BufferedReader(new StringReader(inputStr));

            String line;
            while ((line = br.readLine()) != null) {
                if ("".equals(StringUtils.trim(line))) {
                    continue;
                }

                bw.write(line);
                bw.newLine();
            }
            bw.flush();
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class CustomFileWriter extends Writer {

        private FileWriter fileWriter;
        private StringBuffer stringBuffer;

        public CustomFileWriter(FileWriter fileWriter) {
            this.fileWriter = fileWriter;
            stringBuffer = new StringBuffer();
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            stringBuffer.append(String.valueOf(cbuf, off, len));
        }

        @Override
        public void flush() throws IOException {
            String inputStr = stringBuffer.toString();

            String result = TemplateUtilsMax.changeSpaceLinesToOne(inputStr);

            fileWriter.write(result);
            fileWriter.flush();
        }

        @Override
        public void close() throws IOException {
            fileWriter.flush();
        }
    }

    static class CustomStringWriter extends Writer {

        private StringWriter stringWriter;
        private StringBuffer stringBuffer;

        public CustomStringWriter(StringWriter stringWriter) {
            this.stringWriter = stringWriter;
            stringBuffer = new StringBuffer();
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            stringBuffer.append(String.valueOf(cbuf, off, len));
        }

        @Override
        public void flush() throws IOException {
            String inputStr = stringBuffer.toString();

            String result = TemplateUtilsMax.changeSpaceLinesToOne(inputStr);

            stringWriter.write(result);
            stringWriter.flush();
        }

        @Override
        public void close() throws IOException {
            stringWriter.close();
        }
    }
}
