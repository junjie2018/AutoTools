package fun.junjie.autotools.utils;

public class AssertUtils {
    public static void assertNotNull(Object target, String msg) {
        if (target == null) {
            throw new RuntimeException(msg);
        }
    }
}
