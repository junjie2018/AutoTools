package fun.junjie.autotools.domain.yaml.codec;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import fun.junjie.autotools.constant.ToolsConfig;
import fun.junjie.autotools.domain.yaml.JavaType;
import org.apache.commons.lang3.EnumUtils;

import java.io.IOException;
import java.lang.reflect.Type;

public class JavaTypeCodec implements ObjectSerializer, ObjectDeserializer {

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {

        if (!ToolsConfig.JAVA_TYPE.equals(type.getTypeName())) {
            throw new RuntimeException("Wrong Enum Class");
        }

        //noinspection unchecked
        return (T) JavaType.convert((String) parser.parse());
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        if (object instanceof JavaType) {
            serializer.write(((JavaType) object).getJavaTypeName());
        }
    }
}