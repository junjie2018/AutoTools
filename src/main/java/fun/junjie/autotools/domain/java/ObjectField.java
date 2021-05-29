package fun.junjie.autotools.domain.java;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class ObjectField {
    private String objectFieldName;
    private String objectFieldClassName;
    private String objectFieldHandlerName;

    public ObjectField(String fieldName) {
        this.objectFieldName = fieldName;
        this.objectFieldClassName = StringUtils.capitalize(fieldName);
        this.objectFieldHandlerName = objectFieldClassName + "Handler";
    }
}
