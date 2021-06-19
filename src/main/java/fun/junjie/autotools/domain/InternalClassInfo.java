package fun.junjie.autotools.domain;

import lombok.Data;

import java.util.List;

@Data
public class InternalClassInfo {
    /**
     * 静态内部类的类名称
     */
    private String internalClassName;

    /**
     * 静态内部类的对象名称
     */
    private String internalObjectName;

    /**
     * 静态内部类的描述
     */
    private String internalClassComment;

    /**
     * 静态内部类的字段信息
     */
    private List<FieldItemInfo> fieldItems;
}
