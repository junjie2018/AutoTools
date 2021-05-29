package fun.junjie.autotools.domain.java;

import lombok.Data;

import java.util.List;

@Data
public class ServiceClass {
    private String packageName;
    private List<String> packagesToImport;
    private String entityName;
    private String entityChineseName;
    private String entityDesc;
    private String serviceName;
}
