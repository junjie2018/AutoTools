package fun.junjie.autotools.domain.java;

import fun.junjie.autotools.domain.postgre.ColumnType;
import fun.junjie.autotools.utils.JStringUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class Field {
    private String fieldName;
    private String fieldDesc;
    private JavaType fieldType;
    private String fieldJavaType;

    private List<Annotation> annotationList;

    public Field(String columnName, String columnDesc, ColumnType columnType) {

        // 下划线转驼峰
        columnName = JStringUtils.underlineToCamel(columnName);

        this.fieldName = columnName;
        this.fieldDesc = columnDesc;
        this.fieldType = JavaType.toJavaType(columnType);

        this.annotationList = new ArrayList<>();

        // 之所以在JavaType的枚举上还要fieldJavaType，是因为当数据库类型为jsonb时，JavaType枚举无法处理
        if (this.fieldType == JavaType.Object) {
            this.fieldJavaType = StringUtils.capitalize(this.fieldName);

            Annotation annotation =
                    new Annotation("com.baomidou.mybatisplus.annotation.TableField");

            annotation.addParam(
                    "typeHandler",
                    StringUtils.capitalize(fieldName) + "TypeHandler.class");

            annotationList.add(annotation);

        } else {
            this.fieldJavaType = this.fieldType.getJavaTypeName();
        }
    }
}
