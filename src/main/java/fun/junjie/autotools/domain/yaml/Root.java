package fun.junjie.autotools.domain.yaml;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class Root {

    private List<TableRoot> tables;

    public Root() {
        tables = new ArrayList<>();
    }

    public void put(TableRoot tableRoot) {

        tables.add(tableRoot);
    }
}
