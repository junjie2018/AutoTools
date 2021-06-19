package fun.junjie.autotools.domain;

import lombok.Data;

import java.util.List;

/**
 * 该类的信息是从列描述中解析出来的
 */
@Data
public class EnumInfo {

    /**
     * 枚举类名称
     */
    private String enumClassName;

    /**
     * 枚举对象名称
     */
    private String enumObjectName;

    /**
     * 枚举描述
     */
    private String enumComment;


    private List<EnumItemInfo> enumItems;
}
