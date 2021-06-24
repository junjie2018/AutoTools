package fun.junjie.autotools.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ProcessUtils {
    public static void compareTwoDirs(Path left, Path right) {
        try {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(String.format("%s %s %s",
                    "D:\\Software\\Beyond Compare 3\\BCompare.exe",
                    left.toString(),
                    right.toString()));
        } catch (Exception e) {
            System.out.println("open failure");
        }
    }
}
