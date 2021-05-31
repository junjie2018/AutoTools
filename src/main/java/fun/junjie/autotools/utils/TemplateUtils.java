package fun.junjie.autotools.utils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import fun.junjie.autotools.config.project.ProjectConfig;
import fun.junjie.autotools.domain.config.TplConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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

    private static void printConfig() {
        List<Path> paths = new ArrayList<>();

        walkThrough(Paths.get(TEMPLATES_ROOT), paths::add);

        for (Path path : paths) {
            System.out.println("- tmpFile: " + Paths.get(TEMPLATES_ROOT).relativize(path));
            System.out.println("  outputDir: ");
        }
    }

    private void renderTpl(String tplFile, String fileName, Map<String, Object> renderData) {
        try {
            TplConfig tplConfig = ProjectConfig.getTplConfig(tplFile);

            Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
            configuration.setDirectoryForTemplateLoading(new File(tplConfig.getTplRoot()));
            configuration.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_31).build());

            String outputFile = Paths.get(tplConfig.getOutputDir(), fileName).toString();
            FileWriter fileWriter = new FileWriter(outputFile);

            Template template = configuration.getTemplate(tplFile);
            template.process(renderData, fileWriter);

            fileWriter.flush();

        } catch (Exception e) {
            throw new RuntimeException("Render Template Wrong.");
        }
    }
}
