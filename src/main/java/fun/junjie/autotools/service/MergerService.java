package fun.junjie.autotools.service;

import fun.junjie.autotools.domain.yaml.JavaType;
import fun.junjie.autotools.domain.yaml.TableRoot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MergerService {
    private void mergeTableRootInfos(List<TableRoot> tableRootsFromDb, List<TableRoot> tableRootsFromYaml) {

        Map<String, TableRoot> tblName2TableRootL = new HashMap<>();
        Map<String, TableRoot> tblName2TableRootR = new HashMap<>();

        for (TableRoot tableRoot : tableRootsFromDb) {
            tblName2TableRootL.put(tableRoot.getTblName(), tableRoot);
        }

        for (TableRoot tableRoot : tableRootsFromYaml) {
            tblName2TableRootR.put(tableRoot.getTblName(), tableRoot);
        }

        mergeTableRoot(tblName2TableRootR, tblName2TableRootL);

        for (Map.Entry<String, TableRoot> entry : tblName2TableRootL.entrySet()) {

            TableRoot tableRootR = tblName2TableRootR.get(entry.getKey());
            TableRoot tableRootL = entry.getValue();

            Map<String, TableRoot.ColumnRoot> colName2ColumnRootL = new HashMap<>();
            Map<String, TableRoot.ColumnRoot> colName2ColumnRootR = new HashMap<>();

            for (TableRoot.ColumnRoot column : tableRootR.getColumns()) {
                colName2ColumnRootR.put(column.getColName(), column);
            }

            for (TableRoot.ColumnRoot column : tableRootL.getColumns()) {
                colName2ColumnRootL.put(column.getColName(), column);
            }

            mergeColumnRoot(colName2ColumnRootL, colName2ColumnRootR);


            for (String colName : colName2ColumnRootR.keySet()) {
                TableRoot.ColumnRoot columnRootL = colName2ColumnRootL.get(colName);
                TableRoot.ColumnRoot columnRootR = colName2ColumnRootR.get(colName);

                if (columnRootL.getJavaType() == JavaType.NUMBER_ENUM) {
                    if (columnRootR.getJavaType() == JavaType.NUMBER_ENUM) {
                        mergeEnumRoot(columnRootL.getEnums(), columnRootR.getEnums());
                    }
                }

                if (columnRootL.getJavaType() == JavaType.STRING_ENUM) {
                    if (columnRootR.getJavaType() == JavaType.STRING_ENUM) {
                        mergeEnumRoot(columnRootL.getEnums(), columnRootR.getEnums());
                    }
                }
            }
        }
    }

    private void mergeTableRoot(Map<String, TableRoot> tblName2TableRootL,
                                Map<String, TableRoot> tblName2TableRootR) {
        /*
            融合算法：TableRoot：
                a.新增的tableRoot，从左边同步到右边
                b.删除的tableRoot，从左边同步到右边
                c.修改tblDesc，从左边同步到右边
         */

        // 处理新增的
        for (String tblName : tblName2TableRootL.keySet()) {
            if (!tblName2TableRootR.containsKey(tblName)) {
                tblName2TableRootR.put(tblName, tblName2TableRootR.get(tblName));
            }
        }

        // 处理删除的
        for (String tblName : tblName2TableRootR.keySet()) {
            if (!tblName2TableRootL.containsKey(tblName)) {
                tblName2TableRootR.remove(tblName);
            }
        }

        // 处理tblDesc改变的
        for (String tblName : tblName2TableRootR.keySet()) {
            TableRoot tableRootR = tblName2TableRootR.get(tblName);
            TableRoot tableRootL = tblName2TableRootL.get(tblName);

            if (!tableRootL.getTblName().equals(tableRootR.getTblName())) {
                tableRootR.setTblName(tableRootL.getTblName());
            }
        }
    }


    private void mergeColumnRoot(Map<String, TableRoot.ColumnRoot> colName2ColumnRootL,
                                 Map<String, TableRoot.ColumnRoot> colName2ColumnRootR) {
        /*
            融合算法：ColumnRoot：
                a.新增的columnRoot，从左边同步到右边
                b.删除的columnRoot，从左边同步到右边
                c.修改colDesc，从左边同步到右边
                d.修改javaType，从坐标同步到右边（只针对非枚举、Object类型）
         */

        // 处理新增的
        for (String colName : colName2ColumnRootL.keySet()) {
            if (!colName2ColumnRootR.containsKey(colName)) {
                colName2ColumnRootR.put(colName, colName2ColumnRootR.get(colName));
            }
        }

        // 处理删除的
        for (String colName : colName2ColumnRootR.keySet()) {
            if (!colName2ColumnRootL.containsKey(colName)) {
                colName2ColumnRootR.remove(colName);
            }
        }

        // 处理colDesc改变的
        for (String colName : colName2ColumnRootR.keySet()) {
            TableRoot.ColumnRoot columnRootR = colName2ColumnRootR.get(colName);
            TableRoot.ColumnRoot columnRootL = colName2ColumnRootR.get(colName);

            if (!columnRootR.getColName().equals(columnRootL.getColName())) {
                columnRootR.setColName(columnRootL.getColName());
            }
        }

        // 处理javaType改动
        for (String colName : colName2ColumnRootR.keySet()) {
            TableRoot.ColumnRoot columnRootR = colName2ColumnRootR.get(colName);
            TableRoot.ColumnRoot columnRootL = colName2ColumnRootR.get(colName);

            // 只有左右两边都是简单的类型时，才能够进行merge
            if (!columnRootR.getJavaType().equals(columnRootL.getJavaType())) {
                if (columnRootR.getJavaType().equals(JavaType.DATE)
                        || columnRootR.getJavaType().equals(JavaType.NUMBER)
                        || columnRootR.getJavaType().equals(JavaType.STRING)) {
                    if (columnRootL.getJavaType().equals(JavaType.DATE)
                            || columnRootL.getJavaType().equals(JavaType.NUMBER)
                            || columnRootL.getJavaType().equals(JavaType.STRING)) {
                        columnRootR.setJavaType(columnRootL.getJavaType());
                    }
                }
            }
        }

    }

    private void mergeEnumRoot(TableRoot.EnumRoot enumRootL,
                               TableRoot.EnumRoot enumRootR) {
        /*
            融合算法：EnumRoot
                a.修改enumDesc，从左边同步到右边
                b.新增的enumItem，从左边同步到右边
                c.删除的enumItem，从左边同步到右边
                d.修改itemName，从右边同步到左边
                e.修改itemDesc，从左边同步到右边
         */

        // enumDesc
        if (!enumRootR.getEnumDesc().equals(enumRootL.getEnumDesc())) {
            enumRootR.setEnumDesc(enumRootL.getEnumDesc());
        }


        Map<String, TableRoot.EnumItem> itemName2EnumItemL = new HashMap<>();
        Map<String, TableRoot.EnumItem> itemValue2EnumItemL = new HashMap<>();
        Map<String, TableRoot.EnumItem> itemName2EnumItemR = new HashMap<>();
        Map<String, TableRoot.EnumItem> itemValue2EnumItemR = new HashMap<>();

        for (TableRoot.EnumItem enumItem : enumRootL.getEnumItems()) {
            itemName2EnumItemL.put(enumItem.getItemName(), enumItem);
            itemValue2EnumItemL.put(enumItem.getItemValue(), enumItem);
        }

        for (TableRoot.EnumItem enumItem : enumRootR.getEnumItems()) {
            itemName2EnumItemR.put(enumItem.getItemName(), enumItem);
            itemValue2EnumItemR.put(enumItem.getItemValue(), enumItem);
        }

        // 新增enumItem
        for (String itemValue : itemValue2EnumItemL.keySet()) {
            if (!itemValue2EnumItemR.containsKey(itemValue)) {
                itemValue2EnumItemR.put(itemValue, itemName2EnumItemL.get(itemValue));
            }
        }

        // itemName
        for (String itemValue : itemValue2EnumItemR.keySet()) {
            TableRoot.EnumItem enumItemL = itemValue2EnumItemL.get(itemValue);
            TableRoot.EnumItem enumItemR = itemValue2EnumItemR.get(itemValue);

            if (!enumItemL.getItemName().equals(enumItemR.getItemName())) {
                enumItemL.setItemName(enumItemR.getItemName());
            }
        }

        // itemDesc
        for (String itemValue : itemValue2EnumItemR.keySet()) {
            TableRoot.EnumItem enumItemL = itemValue2EnumItemL.get(itemValue);
            TableRoot.EnumItem enumItemR = itemValue2EnumItemR.get(itemValue);

            if (!enumItemL.getItemDesc().equals(enumItemR.getItemDesc())) {
                enumItemR.setItemDesc(enumItemL.getItemDesc());
            }
        }
    }

    private void mergeInternalClassRoot() {

    }
}
