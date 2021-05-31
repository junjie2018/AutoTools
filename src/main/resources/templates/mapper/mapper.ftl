package ${mapperClass.packageName};

<#list mapperClass.packagesToImport as packageName>
import ${packageName};
</#list>

public interface ${mapperClass.mapperName} extends GeneralDao<${mapperClass.entityName}>{

}