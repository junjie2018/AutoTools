package fun.junjie.autotools.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum DyfConfig {

    /**
     * constant/enums/enum.ftl
     */
    ENUM("constant\\enums\\enum.ftl", ""),


    ;
    private String path;
    private String outputPath;

}
