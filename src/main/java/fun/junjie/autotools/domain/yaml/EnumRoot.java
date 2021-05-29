package fun.junjie.autotools.domain.yaml;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class EnumRoot {

    private String enumName;
    private String enumDesc;
    private List<EnumItem> enumItems;

    @Data
    @NoArgsConstructor
    public static class EnumItem {
        private String enumItemName;
        private String enumItemValue;
        private String enumItemDesc;

        public EnumItem(String enumItemName, String enumItemValue, String enumItemDesc) {
            this.enumItemName = enumItemName;
            this.enumItemValue = enumItemValue;
            this.enumItemDesc = enumItemDesc;
        }
    }

    public EnumRoot(String enumName, String enumDesc) {
        this.enumName = enumName;
        this.enumDesc = enumDesc;
        this.enumItems = new ArrayList<>();
    }

    public void addEnumItem(EnumItem enumItem) {
        enumItems.add(enumItem);
    }
}
