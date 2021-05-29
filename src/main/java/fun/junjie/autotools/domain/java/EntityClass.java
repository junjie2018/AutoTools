package fun.junjie.autotools.domain.java;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class EntityClass {
    private String packageName;
    private List<String> packagesToImport;
    private String entityName;
    private String entityDesc;
    private String entityNameLower;
    private String tableName;
    private String entityChineseName;

    private List<Field> fields;
    private List<InternalClass> internalClasses = new ArrayList<>();
    private List<String> internalClassHandlers = new ArrayList<>();

    @Data
    public static class InternalClass {
        private String internalClassName;
        private String internalClassDesc;

        private List<Field> fields;

        public void addField(String fieldName, String fieldType, String fieldDesc) {
            if (fields == null) {
                fields = new ArrayList<>();
            }
            fields.add(new Field(fieldName, fieldType, fieldDesc));
        }
    }

    @Data
    @AllArgsConstructor
    public static class Field {
        private String fieldName;
        private String fieldNameUpper;
        private String fieldType;
        private String fieldDesc;
        private List<String> annotations;

        public Field(String fieldName, String fieldType, String fieldDesc) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
            this.fieldDesc = fieldDesc;
            this.fieldNameUpper = StringUtils.capitalize(fieldName);

            annotations = new ArrayList<>();
        }
    }

    public void addPackage(String packageName) {
        if (packagesToImport == null) {
            packagesToImport = new ArrayList<>();
        }
        packagesToImport.add(packageName);
    }

    public void addField(Field field) {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        fields.add(field);
    }

    private List<EnumClass.EnumItem> enumItems;
}
