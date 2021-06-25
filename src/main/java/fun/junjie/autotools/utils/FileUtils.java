package fun.junjie.autotools.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class FileUtils {

    public static void walkThrough(String path, Consumer<Path> disposer) {
        walkThrough(Paths.get(path), disposer);
    }

    public static void walkThrough(Path path, Consumer<Path> disposer) {
        if (path == null || !Files.exists(path) || !Files.isDirectory(path)) {
            throw new RuntimeException(path + " Not Exists");
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
}
