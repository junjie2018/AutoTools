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
    private String enumClass;

    /**
     * 枚举对象名称
     */
    private String enumObject;

    /**
     * 枚举描述
     */
    private String enumComment;

    /**
     * 枚举值类型
     */
    private String enumValueType;


    private List<EnumItemInfo> enumItems;
}
