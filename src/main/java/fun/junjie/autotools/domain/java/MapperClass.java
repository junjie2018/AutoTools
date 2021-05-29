package fun.junjie.autotools.domain.java;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MapperClass {
    private String packageName;
    private List<String> packagesToImport = new ArrayList<>();
    private String mapperName;
    private String entityName;
}
