package fun.junjie.autotools.domain.yaml;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ObjectRoot {

    private String objectName;
    private String objectDesc;
    private List<FieldItem> fieldItems;

    @Data
    @NoArgsConstructor
    public static class FieldItem {
        private String fieldName;
        private JavaType fieldType;
        private String fieldDesc;

        private FieldItem(String fieldName, JavaType fieldType, String fieldDesc) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
            this.fieldDesc = fieldDesc;
        }
    }

    public ObjectRoot(String objectName, String objectDesc) {
        this.objectName = objectName;
        this.objectDesc = objectDesc;
        this.fieldItems = new ArrayList<>();
    }

    public void addFieldItem(String fieldName, JavaType fieldType, String fieldDesc) {
        fieldItems.add(new FieldItem(fieldName, fieldType, fieldDesc));
    }
}
