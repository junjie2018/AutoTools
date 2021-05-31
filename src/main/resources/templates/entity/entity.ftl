package ${entityClass.packageName};

<#list entityClass.packagesToImport as packageName>
import ${packageName};
</#list>

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(value = "${entityClass.tableName}", autoResultMap = true)
public class ${entityClass.entityName} extends BasePo{

<#list entityClass.fields as field>
    /**
     * ${field.fieldDesc}
     */
    <#list field.annotations as annotation>
    ${annotation}
    </#list>
    private ${field.fieldType} ${field.fieldName};

</#list>


<#list entityClass.internalClasses as internalClass>

    @Data
    public static class ${internalClass.internalClassName} {
    <#list internalClass.fields as field>
        /**
         * ${field.fieldDesc}
         */
        private ${field.fieldType} ${field.fieldName};
    </#list>
    }

</#list>


<#list entityClass.internalClassHandlers as handler>
    @Slf4j
    @MappedTypes({Object.class})
    @MappedJdbcTypes(JdbcType.VARCHAR)
    public static class ${handler}Handler extends JsonTypeHandler {
        public ${handler}Handler() {
            this.type = ${handler}.class;
        }
    }
</#list>


}