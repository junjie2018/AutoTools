package fun.junjie.autotools.domain.java;

import fun.junjie.autotools.domain.yaml.EnumRoot;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EnumClass {

    private String packageName;
    private List<String> packagesToImport;
    private String enumName;
    private String enumDesc;
    private String valueType;
    private List<EnumItem> enumItems;

    public EnumClass() {
        packagesToImport = new ArrayList<>();
    }

    public void addPackage(String packageName) {
        packagesToImport.add(packageName);
    }

    public void addEnumItem(EnumItem enumItem) {
        if (enumItems == null) {
            enumItems = new ArrayList<>();
        }
        enumItems.add(enumItem);
    }

    @Data
    public static class EnumItem {
        private String enumItemName;
        private String enumItemValue;
        private String enumItemDesc;
    }

}
