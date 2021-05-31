package fun.junjie.autotools.domain.yaml;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class TableRoot {
    private String tblName;
    private String tblDesc;
    private List<ColumnRoot> columns;

    public TableRoot(String tblName, String tblDesc) {
        this.tblName = tblName;
        this.tblDesc = tblDesc;
        columns = new ArrayList<>();
    }

    public void updateColumnDesc(String colName, String colDesc) {
        ColumnRoot columnRoot = findColumnRoot(colName);
        columnRoot.setColDesc(colDesc);
    }

    public void updateColumnJavaType(String colName, JavaType javaType) {
        ColumnRoot columnRoot = findColumnRoot(colName);
        columnRoot.setJavaType(javaType);
    }

    public void addColumn(String colName, String colDesc, JavaType javaType) {
        ColumnRoot columnRoot = new ColumnRoot();
        columnRoot.setColName(colName);
        columnRoot.setColDesc(colDesc);
        columnRoot.setJavaType(javaType);
        this.columns.add(columnRoot);
    }

    public void addEnumRoot(String colName, String enumName, String enumDesc) {
        EnumRoot enumRoot = new EnumRoot();
        enumRoot.setEnumName(enumName);
        enumRoot.setEnumDesc(enumDesc);
        enumRoot.setEnumItems(new ArrayList<>());

        ColumnRoot columnRoot = findColumnRoot(colName);
        columnRoot.setEnums(enumRoot);
    }

    public void addEnumItem(String colName, String itemName, String itemValue, String itemDesc) {
        EnumItem enumItem = new EnumItem();
        enumItem.setItemName(itemName);
        enumItem.setItemValue(itemValue);
        enumItem.setItemDesc(itemDesc);

        ColumnRoot columnRoot = findColumnRoot(colName);
        columnRoot.getEnums().getEnumItems().add(enumItem);
    }

    public void addObject(String colName, String objName, String objDesc) {
        ObjectRoot objectRoot = new ObjectRoot();
        objectRoot.setObjName(objName);
        objectRoot.setObjDesc(objDesc);
        objectRoot.setFields(new ArrayList<>());

        ColumnRoot columnRoot = findColumnRoot(colName);

        if (columnRoot.getObjects() == null) {
            columnRoot.setObjects(new ArrayList<>());
            columnRoot.getObjects().add(objectRoot);
        }
    }

    public void addObjectField(String colName, String objName, String fieldName, JavaType fieldType, String fieldDesc) {
        ObjectField objectField = new ObjectField();
        objectField.setFieldName(fieldName);
        objectField.setFieldType(fieldType);
        objectField.setFieldDesc(fieldDesc);

        ObjectRoot object = findObject(colName, objName);
        object.getFields().add(objectField);
    }

    private ColumnRoot findColumnRoot(String colName) {
        for (ColumnRoot column : columns) {
            if (column.getColName().equals(colName)) {
                return column;
            }
        }
        throw new RuntimeException("Find ColumnRoot Wrong.");
    }

    private ObjectRoot findObject(String colName, String objName) {
        ColumnRoot columnRoot = findColumnRoot(colName);
        for (ObjectRoot object : columnRoot.getObjects()) {
            if (object.getObjName().equals(objName)) {
                return object;
            }
        }
        throw new RuntimeException("Find ObjectRoot Wrong.");
    }

    @Data
    @SuppressWarnings("WeakerAccess")
    public static class ColumnRoot {
        private String colName;
        private String colDesc;
        private JavaType javaType;
        private EnumRoot enums;
        private List<ObjectRoot> objects;
    }

    @Data
    @SuppressWarnings("WeakerAccess")
    public static class EnumRoot {
        private String enumName;
        private String enumDesc;
        private List<EnumItem> enumItems;
    }

    @Data
    @SuppressWarnings("WeakerAccess")
    public static class EnumItem {
        private String itemName;
        private String itemValue;
        private String itemDesc;
    }

    @Data
    @SuppressWarnings("WeakerAccess")
    public static class ObjectRoot {
        private String objName;
        private String objDesc;
        private List<ObjectField> fields;
    }

    @Data
    @SuppressWarnings("WeakerAccess")
    public static class ObjectField {
        private String fieldName;
        private JavaType fieldType;
        private String fieldDesc;
    }
}
