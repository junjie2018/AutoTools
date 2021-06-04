package com.sdstc.authcenter.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ${enumClass.enumDesc}
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ${enumClass.enumJavaNameCapitalized} {

<#list enumClass.enumItems as enumItem>
    /**
     * ${enumItem.enumItemDesc}
     */
    ${enumItem.enumItemNameUpper}("${enumItem.enumItemValue}"),

</#list>
    ;

    @Getter
    private String value;

    public static ${enumClass.enumJavaNameCapitalized} convert(String inputValue) {

        for (${enumClass.enumJavaNameCapitalized} enumItem : ${enumClass.enumJavaNameCapitalized}.values()) {
            if (enumItem.getValue().equals(inputValue)) {
                return enumItem;
            }
        }
        throw new RuntimeException("Enum Transfer Wrong.");
    }
}