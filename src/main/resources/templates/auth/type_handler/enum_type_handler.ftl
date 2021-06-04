package com.sdstc.authcenter.type_handlers.enums;

import com.sdstc.authcenter.enums.${enumClass.enumJavaNameCapitalized};
import com.sdstc.authcenter.type_handlers.AbstractEnumTypeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@Slf4j
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class ${enumClass.enumJavaNameCapitalized}TypeHandler extends AbstractEnumTypeHandler<${enumClass.enumJavaNameCapitalized}> {
    @Override
    protected ${enumClass.enumJavaNameCapitalized} parseValue(String inputParam) {
        return ${enumClass.enumJavaNameCapitalized}.convert(inputParam);
    }

    @Override
    protected String toValue(${enumClass.enumJavaNameCapitalized} ${enumClass.enumJavaNameCapitalized}) {
        return ${enumClass.enumJavaNameCapitalized}.getValue();
    }
}
