package com.kryptoncell.gen_entity;

import com.kryptoncell.gen_db.TableColumnMetadata;
import com.kryptoncell.utils.StringHelper;

import java.util.*;
import java.util.stream.Collectors;

public final class EntityMetadata {

    private final String packageName; // entity类的包名
    private final String packageStatement; //包语句
    private final Set<String> importStatements; // entity类的import语句
    private final String entityClassName; // entity类的类名
    private final String entityComment; // entity类注释
    private final List<EntityFieldMetadata> fields; // entity所有的成员变量
    private final String entityFileName; // entity文件名

    public EntityMetadata(String basePackage, boolean remainTablePrefix, String dbTable, String dbTableComment, List<TableColumnMetadata> dbColumns) {
        // entity所在的包
        this.packageName = basePackage + ".entity";

        // entity包语句
        this.packageStatement = "package " + this.packageName + ";";

        // entity所有的field
        this.fields = new ArrayList<>();
        for (var col : dbColumns) {
            this.fields.add(new EntityFieldMetadata(col));
        }

        // entity的类名
        if (remainTablePrefix) {
            // 如果保留表的前缀，那么t_user_basic将生成为TUserBasicEntity
            this.entityClassName = StringHelper.toCamelName(dbTable, false) + "Entity";
        } else {
            // 如果不保留表的前缀，那么t_user_basic将生成UserBasicEntity
            var strArr = dbTable.split("_");
            var prefix = strArr[0] + "_";

            this.entityClassName = StringHelper.toCamelName(dbTable.substring(prefix.length()), false) + "Entity";
        }

        // imports语句
        this.importStatements = new HashSet<>();
        this.importStatements.add("import java.util.Objects;");
        for (var fd : this.fields) {
            if (fd.getFieldEnumType()) {
                this.importStatements.add("import java.util.Arrays;");
                this.importStatements.add("import com.fasterxml.jackson.annotation.JsonCreator;");
                this.importStatements.add("import com.fasterxml.jackson.annotation.JsonValue;");
            }
            if (fd.getFieldJavaType().equalsIgnoreCase("LocalDateTime")) {
                this.importStatements.add("import java.time.LocalDateTime;");
            }
            if (fd.getFieldJavaType().equalsIgnoreCase("LocalDate")) {
                this.importStatements.add("import java.time.LocalDate;");
            }
        }

        // 文件名
        this.entityFileName = this.entityClassName + ".java";

        // entity类注释
        this.entityComment = "/**\n * " + dbTableComment + "\n */";
    }

    /**
     * 生成要写入文件的字符串
     */
    public String toFileWriteString() {
        var sb = new StringBuilder();

        // package 语句
        sb.append(this.packageName).append("\n\n");

        // import语句
        for (var ip : this.importStatements) {
            sb.append(ip).append("\n");
        }
        sb.append("\n");

        // 类注释
        sb.append(this.entityComment).append("\n");

        // 类定义
        sb.append("public final class ").append(this.entityClassName).append(" {\n\n");

        // 类成员变量
        for (var fd : this.fields) {
            // 成员注释
            sb.append(fd.getFieldComment()).append("\n");
            // 成员声明
            sb.append("\rprivate ").append(fd.getFieldJavaType()).append(" ").append(fd.getFieldName()).append(";\n");
        }
        sb.append("\n");

        // 枚举类型变量
        for (var fd : this.fields) {
            if (!fd.getFieldEnumType()) {
                continue;
            }

            // 枚举类声明
            sb.append("\rpublic enum ").append(fd.getFieldJavaType()).append(" {\n");
            // 枚举变量
            sb.append("\r\rENUM_NULL(null),\n");
            sb.append("\r\rENUM_ONE(1),\n");
            sb.append("\r\rENUM_TWO(2),\n");
            sb.append("\r\rENUM_THREE(3),\n");
            sb.append("\r\rENUM_FOUR(4);\n");

            // 枚举类成员变量
            sb.append("\r\rprivate final Integer value;\n\n");

            // 枚举构造方法
            sb.append("\r\r").append(fd.getFieldJavaType()).append("(").append("Integer value").append(") {\n");
            sb.append("\r\r\rthis.value = value;\n");
            sb.append("\r\r}\n\n");

            // 枚举的getter值方法
            sb.append("\r\r@JsonValue\n");
            sb.append("\r\rpublic Integer getValue() {\n");
            sb.append("\r\r\rreturn this.value;\n");
            sb.append("\r\r}\n\n");

            // 枚举的getter枚举值方法
            sb.append("\r\r@JsonCreator\n");
            sb.append("\r\rpublic static ").append(fd.getFieldJavaType()).append(" getByValue(Integer wantValue) {\n");
            sb.append("\r\r\rreturn Arrays.stream(StatusEnum.values())\n");
            sb.append("\r\r\r\r\r.filter(e -> Objects.equals(e.getValue(), wantValue))\n");
            sb.append("\r\r\r\r\r.findFirst()\n");
            sb.append("\r\r\r\r\r.orElseThrow(\n");
            sb.append("\r\r\r\r\r\r\r() -> new RuntimeException(String.format(\"").append(this.entityClassName).append(".").append(fd.getFieldJavaType()).append("(%d)枚举值不存在\", wantValue))\n");
            sb.append("\r\r\r\r\r);\n");
            sb.append("\r\r}\n");

            // 枚举类声明结束
            sb.append("\r}\n\n");
        }

        // 所有的getter和setter方法
        for (var fd : this.fields) {
            // getter方法
            sb.append("\r@SuppressWarnings(\"unused\")\n");
            sb.append("\rpublic ").append(fd.getFieldJavaType()).append(" get").append(fd.getFieldGetterMethodName()).append("() {\n");
            sb.append("\r\rreturn ").append(fd.getFieldName()).append(";\n");
            sb.append("\r}\n\n");

            // setter方法
            sb.append("\r@SuppressWarnings(\"unused, UnusedReturnValue\")\n");
            sb.append("\rpublic ").append(this.entityClassName).append(" ").append(fd.getFieldSetterMethodName()).append("(").append(fd.getFieldJavaType()).append(" ").append(fd.getFieldName()).append(") {\n");
            sb.append("\r\rthis.").append(fd.getFieldName()).append(" = ").append(fd.getFieldName()).append(";\n");
            sb.append("\r\rreturn this;\n");
            sb.append("\r}\n\n");
        }

        // equals()方法
        sb.append("\r@Override\n");
        sb.append("\rpublic boolean equals(Object o) {\n");
        sb.append("\r\rif (this == o) {\n");
        sb.append("\r\r\rreturn true;;\n");
        sb.append("\r\r}\n");
        sb.append("\r\rif (o == null || getClass() != o.getClass()) {\n");
        sb.append("\r\r\rreturn false;\n");
        sb.append("\r\r}\n");
        sb.append("\r\rvar that = ").append("(").append(this.entityClassName).append(") o;\n");
        sb.append("\r\rreturn ")
                .append(
                        this.fields.stream()
                                .map(e -> "Objects.equals(" + e.getFieldName() + ", that." + e.getFieldName() + ")")
                                .collect(Collectors.joining(" && "))
                )
                .append(";\n");

        sb.append("\r}\n\n");

        // hashCode()方法
        sb.append("\r@Override\n");
        sb.append("\rpublic int hashCode() {\n");
        sb.append("\r\rreturn Objects.hash(")
                .append(
                        this.fields.stream()
                                .map(EntityFieldMetadata::getFieldName)
                                .collect(Collectors.joining(", "))
                )
                .append(");\n");
        sb.append("\r}\n\n");

        // toString()方法
        sb.append("\r@Override\n");
        sb.append("\rpublic String toString() {\n");
        sb.append("\r\rreturn ").append(this.getEntityClassName()).append("{\" +\n");
        sb.append(
                this.fields.stream()
                        .map(e -> "\"" + e.getFieldName() + "=\" + " + e.getFieldName())
                        .collect(Collectors.joining(" +\n\", \" + "))
                )
                .append(" +\n\"}\";");
        sb.append("\r}\n\n");

        // 类定义结束
        sb.append("}\n");

        return sb.toString();
    }

    @SuppressWarnings("unused")
    public String getPackageName() {
        return packageName;
    }

    @SuppressWarnings("unused")
    public Set<String> getImportStatements() {
        return importStatements;
    }

    @SuppressWarnings("unused")
    public String getEntityClassName() {
        return entityClassName;
    }

    @SuppressWarnings("unused")
    public List<EntityFieldMetadata> getFields() {
        return fields;
    }

    @SuppressWarnings("unused")
    public String getPackageStatement() {
        return packageStatement;
    }

    @SuppressWarnings("unused")
    public String getEntityFileName() {
        return entityFileName;
    }
}
