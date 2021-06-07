package fun.junjie.autotools.utils;

import com.alibaba.fastjson.JSON;

public class ObjectUtils {
    public static <T> T deepCopy(Object source, Class<T> clazz) {
        String jsonStr = JSON.toJSONString(source);
        return JSON.parseObject(jsonStr, clazz);
    }
}
