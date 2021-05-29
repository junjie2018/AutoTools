package fun.junjie.autotools.domain.yaml;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
public class ColumnRoot {
    private String columnName;
    private String columnDesc;
    private JavaType javaType;
    private EnumRoot enumRoot;
    private List<ObjectRoot> objectRoots;

    public ColumnRoot(String columnName, String columnDesc) {
        this.columnName = columnName;
        this.columnDesc = columnDesc;
    }

    public void setJavaType(JavaType javaType) {
        this.javaType = javaType;
    }

    public void addEnumRoot(EnumRoot enumRoot) {
        this.enumRoot = enumRoot;
    }

    public void addObjectRoot(ObjectRoot objectRoot) {
        if (objectRoots == null) {
            objectRoots = new ArrayList<>();
        }
        this.objectRoots.add(objectRoot);
    }
}
