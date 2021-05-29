package com.sdstc.dyf.meta.core.type_handler.enums;

import com.sdstc.dyf.meta.common.constant.enums.${enumClass.enumName};
import com.sdstc.dyf.meta.core.type_handler.AbstractEnumTypeHandler;

public class ${enumClass.enumName}TypeHandler extends AbstractEnumTypeHandler<${enumClass.enumName}> {
    @Override
    protected ${enumClass.enumName} parseValue(String inputParam) {
        return ${enumClass.enumName}.convert(inputParam);
    }

    @Override
    protected String toValue(${enumClass.enumName} isModelRequired) {
        return isModelRequired.getValue();
    }
}
