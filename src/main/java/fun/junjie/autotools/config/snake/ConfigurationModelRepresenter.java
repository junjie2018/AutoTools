package fun.junjie.autotools.config.snake;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.util.*;

public class ConfigurationModelRepresenter extends Representer {


    private String[] fieldIndexes;

    {
        fieldIndexes = new String[]{
                "tableName",
                "tableDesc",
                "columns",
                "columnName",
                "javaType",
                "columnDesc",
                "enumRoot",
                "objectRoot",
                "enumName",
                "enumDesc",
                "enumItems",
                "enumItemName",
                "enumItemValue",
                "enumItemDesc",
                "objectName",
                "objectDesc",
                "fieldItems",
                "fieldName",
                "fieldType",
                "fieldDesc",
        };
    }


    public ConfigurationModelRepresenter() {
        super();
    }

    public ConfigurationModelRepresenter(DumperOptions options) {
        super(options);
    }

    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        if (propertyValue == null) {
            return null;
        }
        return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
    }

    @Override
    protected Set<Property> getProperties(Class<?> type) {
        Set<Property> propertySet = getPropertyUtils().getProperties(type);

        List<Property> propsList = new ArrayList<>(propertySet);
        propsList.sort(new BeanPropertyComparator());

        return new LinkedHashSet<>(propsList);
    }

    private int findFieldIndex(String fieldName) {
        for (int i = 0; i < fieldIndexes.length; i++) {
            if (fieldName.equals(fieldIndexes[i])) {
                return i;
            }
        }
        return 1000;
    }

    class BeanPropertyComparator implements Comparator<Property> {
        @Override
        public int compare(Property p1, Property p2) {

            int fieldIndex1 = findFieldIndex(p1.getName());
            int fieldIndex2 = findFieldIndex(p2.getName());

            return Integer.compare(fieldIndex1, fieldIndex2);
        }
    }
}
