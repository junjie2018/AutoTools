package fun.junjie.autotools.domain.java;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Annotation {
    private String annotationName;
    private String annotationFullPath;
    private List<Param> annotationParams;

    public Annotation(String annotationFullPath) {
        String[] split = annotationFullPath.split("\\.");

        this.annotationName = split[0];
        this.annotationFullPath = annotationFullPath;

        this.annotationParams = new ArrayList<>();
    }

    public void addParam(String paramName, String paramValue) {
        this.annotationParams.add(new Param(
                paramName, paramValue));
    }

    @Data
    @AllArgsConstructor
    public static class Param {
        private String paramName;
        private String paramValue;
    }
}
