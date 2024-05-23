package com.kryptoncell.gen_entity;

import com.kryptoncell.gen_db.TableColumnMetadata;
import com.kryptoncell.utils.StringHelper;

/**
 * entity的成员变量
 */
public class EntityFieldMetadata {

    private final String fieldComment; // 成员变量注释
    private final String fieldName; // 成员变量名
    private final String fieldJavaType; // 成员变量的java类型
    private final Boolean isFieldEnumType; // 成员变量是否是java的enum类型
    private final Boolean isFieldBooleanType; // 成员变量是否是java的boolean类型

    public EntityFieldMetadata(TableColumnMetadata columnMetadata) {
        this.fieldComment = this.genFieldComment(columnMetadata.comment());
        this.fieldName = this.genFieldName(columnMetadata.columnName(), columnMetadata.dataType());
        this.fieldJavaType = this.genFieldJavaType(columnMetadata.columnName(), columnMetadata.dataType());

    }

    /**
     * 生成列注释
     */
    private String genFieldComment(String columnComment) {
        return "// " + columnComment;
    }

    /**
     * 根据表的列名和列类型，来生成entity的成员变量名。
     * 成员变量名遵循驼峰命名法.
     * 如果是tinyint类型，且以is_开头，则要将开头is去掉
     */
    private String genFieldName(String columnName, String dataType) {
        if (columnName.toLowerCase().startsWith("is_") && dataType.equalsIgnoreCase("tinyint")) {
            return StringHelper.toCamelName(columnName.substring(3), true);
        }

        return StringHelper.toCamelName(columnName, true);
    }

    /**
     * 获取entity成员变量的类型
     */
    private String genFieldJavaType(String columnName, String dataType) {
        // 先判定boolean类型
        // 所有tinyint类型的列，只要不是boolean类型的，就是Enum类型的
        if (dataType.equalsIgnoreCase("tinyint")) {
            if (columnName.toLowerCase().startsWith("is_") || columnName.toLowerCase().startsWith("has_")) {
                return "Boolean";
            } else {
                return this.genFieldName(columnName, dataType) + "Enum";
            }
        }

        // 再判定整型
        // 所有的bigint，都是Long
        if (dataType.equalsIgnoreCase("bigint")) {
            return "Long";
        }

        // 对于int，如果是id字段则为Long，如果为其他则为Integer
        if (dataType.equalsIgnoreCase("int")) {
            if (columnName.equalsIgnoreCase("id")) {
                return "Long";
            } else {
                return "Integer";
            }
        }

        // 对于其他的整数，比如smallint等，统一映射为integer
        if (dataType.toLowerCase().endsWith("int")) {
            return "Integer";
        }

        // 再判定时间
        if (dataType.equalsIgnoreCase("date")) {
            return "LocalDate";
        }
        if (dataType.equalsIgnoreCase("datetime") || dataType.equalsIgnoreCase("timestamp")) {
            return "LocalDateTime";
        }

        // 最后，剩余的变量，全部都映射为字符串
        return "String";
    }

}
