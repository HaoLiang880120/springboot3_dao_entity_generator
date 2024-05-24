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
    private final String relateTableName; // 关联的表名
    private final Boolean hasDeletedField; // entity是否有deleted成员

    public EntityMetadata(String basePackage, boolean remainTablePrefix, String dbTable, String dbTableComment, List<TableColumnMetadata> dbColumns) {
        // entity关联的表名
        this.relateTableName = dbTable;

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

        // 是否有deleted成员变量
        this.hasDeletedField = this.fields
                .stream()
                .anyMatch(fd -> "deleted".equals(fd.getFieldName()) || "delete".equals(fd.getFieldName()));

        // 文件名
        this.entityFileName = this.entityClassName + ".java";

        // entity类注释
        this.entityComment = "/**\n * 对应`" + dbTable + "`表\n * " + dbTableComment + "\n */";
    }

    /**
     * 生成要写入文件的字符串
     */
    public String toFileWriteString() {
        var sb = new StringBuilder();

        // package 语句
        sb.append(this.packageStatement).append("\n\n");

        // import语句
        for (var ip : this.importStatements) {
            sb.append(ip).append("\n");
        }
        sb.append("\n");

        // 类注释
        sb.append(this.entityComment).append("\n");

        // 类定义
        sb.append("public final class ").append(this.entityClassName).append(" {\n");

        // 类成员变量
        for (var fd : this.fields) {
            // 成员注释
            sb.append("\t").append(fd.getFieldComment()).append("\n");
            // 成员声明
            sb.append("\tprivate ").append(fd.getFieldJavaType()).append(" ").append(fd.getFieldName()).append(";\n");
        }
        sb.append("\n");

        // 枚举类型变量
        for (var fd : this.fields) {
            if (!fd.getFieldEnumType()) {
                continue;
            }

            // 枚举类声明
            sb.append("\tpublic enum ").append(fd.getFieldJavaType()).append(" {\n");
            // 枚举变量
            sb.append("\t\tENUM_NULL(null),\n");
            sb.append("\t\tENUM_ONE(1),\n");
            sb.append("\t\tENUM_TWO(2),\n");
            sb.append("\t\tENUM_THREE(3),\n");
            sb.append("\t\tENUM_FOUR(4);\n\n");

            // 枚举类成员变量
            sb.append("\t\tprivate final Integer value;\n\n");

            // 枚举构造方法
            sb.append("\t\t").append(fd.getFieldJavaType()).append("(").append("Integer value").append(") {\n");
            sb.append("\t\t\tthis.value = value;\n");
            sb.append("\t\t}\n\n");

            // 枚举的getter值方法
            sb.append("\t\t@JsonValue\n");
            sb.append("\t\tpublic Integer getValue() {\n");
            sb.append("\t\t\treturn this.value;\n");
            sb.append("\t\t}\n\n");

            // 枚举的getter枚举值方法
            sb.append("\t\t@JsonCreator\n");
            sb.append("\t\tpublic static ").append(fd.getFieldJavaType()).append(" getByValue(Integer wantValue) {\n");
            sb.append("\t\t\treturn Arrays.stream(").append(fd.getFieldJavaType()).append(".values())\n");
            sb.append("\t\t\t\t\t.filter(e -> Objects.equals(e.getValue(), wantValue))\n");
            sb.append("\t\t\t\t\t.findFirst()\n");
            sb.append("\t\t\t\t\t.orElseThrow(\n");
            sb.append("\t\t\t\t\t\t\t() -> new RuntimeException(String.format(\"").append(this.entityClassName).append(".").append(fd.getFieldJavaType()).append("(%d)枚举值不存在\", wantValue))\n");
            sb.append("\t\t\t\t\t);\n");
            sb.append("\t\t}\n");

            // 枚举类声明结束
            sb.append("\t}\n\n");
        }

        // 所有的getter和setter方法
        for (var fd : this.fields) {
            // getter方法
            sb.append("\t@SuppressWarnings(\"unused\")\n");
            sb.append("\tpublic ").append(fd.getFieldJavaType()).append(" ").append(fd.getFieldGetterMethodName()).append("() {\n");
            sb.append("\t\treturn ").append(fd.getFieldName()).append(";\n");
            sb.append("\t}\n\n");

            // setter方法
            sb.append("\t@SuppressWarnings(\"unused, UnusedReturnValue\")\n");
            sb.append("\tpublic ").append(this.entityClassName).append(" ").append(fd.getFieldSetterMethodName()).append("(").append(fd.getFieldJavaType()).append(" ").append(fd.getFieldName()).append(") {\n");
            sb.append("\t\tthis.").append(fd.getFieldName()).append(" = ").append(fd.getFieldName()).append(";\n");
            sb.append("\t\treturn this;\n");
            sb.append("\t}\n\n");
        }

        // equals()方法
        sb.append("\t@Override\n");
        sb.append("\tpublic boolean equals(Object o) {\n");
        sb.append("\t\tif (this == o) {\n");
        sb.append("\t\t\treturn true;\n");
        sb.append("\t\t}\n");
        sb.append("\t\tif (o == null || getClass() != o.getClass()) {\n");
        sb.append("\t\t\treturn false;\n");
        sb.append("\t\t}\n");
        sb.append("\t\tvar that = ").append("(").append(this.entityClassName).append(") o;\n");
        sb.append("\t\treturn ")
                .append(
                        this.fields.stream()
                                .map(e -> "Objects.equals(" + e.getFieldName() + ", that." + e.getFieldName() + ")")
                                .collect(Collectors.joining(" && "))
                )
                .append(";\n");

        sb.append("\t}\n\n");

        // hashCode()方法
        sb.append("\t@Override\n");
        sb.append("\tpublic int hashCode() {\n");
        sb.append("\t\treturn Objects.hash(")
                .append(
                        this.fields.stream()
                                .map(EntityFieldMetadata::getFieldName)
                                .collect(Collectors.joining(", "))
                )
                .append(");\n");
        sb.append("\t}\n\n");

        // toString()方法
        sb.append("\t@Override\n");
        sb.append("\tpublic String toString() {\n");
        sb.append("\t\treturn \"").append(this.getEntityClassName()).append("{\" +\n\t\t\t\t");
        sb.append(
                this.fields.stream()
                        .map(e -> "\"" + e.getFieldName() + "=\" + " + e.getFieldName())
                        .collect(Collectors.joining(" +\n\t\t\t\t\", \" + "))
                )
                .append(" +\n\t\t\t\t\"}\";\n");
        sb.append("\t}\n\n");

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

    @SuppressWarnings("unused")
    public String getEntityComment() {
        return entityComment;
    }

    @SuppressWarnings("unused")
    public String getRelateTableName() {
        return relateTableName;
    }

    @SuppressWarnings("unused")
    public Boolean getHasDeletedField() {
        return hasDeletedField;
    }
}
