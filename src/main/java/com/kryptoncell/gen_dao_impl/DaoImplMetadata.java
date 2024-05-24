package com.kryptoncell.gen_dao_impl;

import com.kryptoncell.gen_entity.EntityMetadata;

import java.util.HashSet;
import java.util.Set;

public final class DaoImplMetadata {

    private final String fileName; // daoImpl文件名 xxx.java
    private final String packageName; // daoImpl所在包名
    private final String packageStatement; // package语句
    private final String className; // daoImpl类名
    private final String implementInterfaceName; // daoImpl实现的接口名
    private final Set<String> importStatements = new HashSet<>(); // daoImpl文件import语句
    private final EntityMetadata relateEntityMetadata; // daoImpl关联的entity

    public DaoImplMetadata(String basePackage, EntityMetadata relateEntityMetadata) {
        // package name
        this.packageName = basePackage + ".dao.impl";
        this.packageStatement = "package " + this.packageName + ";";

        var entityName = relateEntityMetadata.getEntityClassName();
        // class name
        var entityNameSubstring = entityName.substring(0, entityName.length() - "Entity".length());
        this.className = entityNameSubstring + "DaoImpl";
        // implement Interface Name
        this.implementInterfaceName = entityNameSubstring + "Dao";
        // file name
        this.fileName = this.className + ".java";

        // import statements
        // 引入dao接口
        this.importStatements.add("import " + basePackage + ".dao." + this.implementInterfaceName + ";");
        // 引入entity
        this.importStatements.add("import " + basePackage + ".entity." + relateEntityMetadata.getEntityClassName() + ";");
        this.importStatements.add("import org.springframework.jdbc.core.JdbcTemplate;");
        this.importStatements.add("import org.springframework.jdbc.core.RowMapper;");
        this.importStatements.add("import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;");
        this.importStatements.add("import org.springframework.jdbc.core.simple.JdbcClient;");
        this.importStatements.add("import org.springframework.jdbc.core.simple.SimpleJdbcInsert;");
        this.importStatements.add("import org.springframework.stereotype.Repository;");
        this.importStatements.add("import java.util.*;");
        this.importStatements.add("import static java.util.Objects.nonNull;");
        for (var entityField : relateEntityMetadata.getFields()) {
            if ("LocalDate".equals(entityField.getFieldJavaType())) {
                this.importStatements.add("import java.time.LocalDate;");
                continue;
            }
            if ("LocalDateTime".equals(entityField.getFieldJavaType())) {
                this.importStatements.add("import java.time.LocalDateTime;");
            }
        }

        // relate entity
        this.relateEntityMetadata = relateEntityMetadata;
    }


    public String toFileWriteString() {
        var sb = new StringBuilder();

        // package 语句
        sb.append(this.packageStatement).append("\n\n");

        // import语句
        for (var ip : this.importStatements) {
            sb.append(ip).append("\n");
        }
        sb.append("\n");

        // daoImpl类定义
        sb.append("@Repository\n");
        sb.append("public class ").append(this.className).append(" implements ").append(this.implementInterfaceName).append(" {\n\n");

        // daoImpl类成员变量
        sb.append("\tprivate final JdbcClient jdbcClient;\n");
        sb.append("\tprivate final SimpleJdbcInsert simpleJdbcInsert;\n\n");

        // daoImpl构造方法
        sb.append("\tpublic ").append(this.className).append("(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate) {\n");
        sb.append("\t\tthis.jdbcClient = jdbcClient;\n");
        sb.append("\t\tthis.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)\n");
        sb.append("\t\t\t\t.withTableName(\"").append(this.relateEntityMetadata.getRelateTableName()).append("\")\n");
        sb.append("\t\t\t\t.usingGeneratedKeyColumns(\"id\");\n");
        sb.append("\t}\n\n");

        // buildRowMapper()方法
        sb.append("\t/**\n");
        sb.append("\t * 数据库表属性与实体entity的映射\n");
        sb.append("\t */\n");
        sb.append("\tprivate RowMapper<").append(this.relateEntityMetadata.getEntityClassName()).append("> buildRowMapper() {\n");
        sb.append("\t\treturn (rs, rowNum) -> new ").append(this.relateEntityMetadata.getEntityClassName()).append("()\n");
        for (var i = 0; i < this.relateEntityMetadata.getFields().size(); i++) {
            var entityField = this.relateEntityMetadata.getFields().get(i);

            if (entityField.getFieldEnumType()) {
                // 如果entity的成员变量是枚举类型
                sb.append("\t\t\t\t.").append(entityField.getFieldSetterMethodName()).append("(\n");
                sb.append("\t\t\t\t\t\t").append(this.relateEntityMetadata.getEntityClassName()).append(".").append(entityField.getFieldJavaType()).append(".getByValue(\n");
                sb.append("\t\t\t\t\t\t\t\trs.getObject(\"").append(entityField.getRelateTableColumnName()).append("\", ").append(entityField.getFieldJavaType()).append(".class)\n");
                sb.append("\t\t\t\t\t\t)\n");
                sb.append("\t\t\t\t)");
            } else {
                // 如果是非枚举类型变量
                sb.append("\t\t\t\t.").append(entityField.getFieldSetterMethodName()).append("(rs.getObject(\"").append(entityField.getRelateTableColumnName()).append("\", ").append(entityField.getFieldJavaType()).append(".class))");
            }

            // 如果迭代到最后一个元素，则在末尾加上分号
            if (i == this.relateEntityMetadata.getFields().size() - 1) {
                sb.append(";");
            }
            sb.append("\n");
        }
        sb.append("\t}\n\n");

        // buildParamMapForInsertion() 方法
        sb.append("\t/**\n");
        sb.append("\t * 根据entity对象构建 `表属性名 => 值` 映射，用以插入表\n");
        sb.append("\t */\n");
        sb.append("\t@SuppressWarnings(\"Duplicates\")\n");
        sb.append("\tprivate Map<String, ?> buildParamMapForInsertion(").append(this.relateEntityMetadata.getEntityClassName()).append(" entity) {\n");
        sb.append("\t\tvar paramMap = new HashMap<String, Object>();\n");
        for (var entityField : relateEntityMetadata.getFields()) {
            if ("id".equals(entityField.getFieldName())) {
                continue;
            }
            if (entityField.getFieldEnumType()) {
                sb.append("\t\tparamMap.put(\"").append(entityField.getRelateTableColumnName()).append("\", entity.").append(entityField.getFieldGetterMethodName()).append("().getValue());\n");
            } else {
                sb.append("\t\tparamMap.put(\"").append(entityField.getRelateTableColumnName()).append("\", entity.").append(entityField.getFieldGetterMethodName()).append("());\n");
            }
        }
        sb.append("\t\treturn paramMap;\n");
        sb.append("\t}\n\n");

        // saveBatch() 方法
        sb.append("\t@Override\n");
        sb.append("\t@SuppressWarnings(\"Duplicates\")\n");
        sb.append("\tpublic void saveBatch(Collection<").append(this.relateEntityMetadata.getEntityClassName()).append("> entities) {\n");
        sb.append("\t\tvar paramMaps = entities.stream()\n");
        sb.append("\t\t\t\t.map(this::buildParamMapForInsertion)\n");
        sb.append("\t\t\t\t.toList();\n\n");
        sb.append("\t\tthis.simpleJdbcInsert.executeBatch(\n");
        sb.append("\t\t\t\tSqlParameterSourceUtils.createBatch(paramMaps)\n");
        sb.append("\t\t);\n");
        sb.append("\t}\n\n");

        // save() 方法
        sb.append("\t@Override\n");
        sb.append("\t@SuppressWarnings(\"Duplicates\")\n");
        sb.append("\tpublic void save(").append(this.relateEntityMetadata.getEntityClassName()).append(" entity) {\n");
        sb.append("\t\tvar id = this.simpleJdbcInsert.executeAndReturnKey(\n");
        sb.append("\t\t\t\tthis.buildParamMapForInsertion(entity)\n");
        sb.append("\t\t);\n");
        sb.append("\t\t// 将数据库生成的主键值赋予entity\n");
        sb.append("\t\tentity.setId(id.longValue());\n");
        sb.append("\t};\n\n");

        // getById() 方法
        sb.append("\t@Override\n");
        sb.append("\t@SuppressWarnings(\"Duplicates\")\n");
        sb.append("\tpublic Optional<").append(this.relateEntityMetadata.getEntityClassName()).append("> getById(Long id) {\n\n");
        sb.append("\t\tvar sql = \"SELECT * FROM `").append(this.relateEntityMetadata.getRelateTableName()).append("` WHERE `id` = :id\";\n\n");
        sb.append("\t\treturn this.jdbcClient\n");
        sb.append("\t\t\t\t.sql(sql)\n");
        sb.append("\t\t\t\t.param(\"id\", id)\n");
        sb.append("\t\t\t\t.query(this.buildRowMapper())\n");
        sb.append("\t\t\t\t.optional();\n");
        sb.append("\t}\n\n");

        // listByIds() 方法
        sb.append("\t@Override\n");
        sb.append("\t@SuppressWarnings(\"Duplicates\")\n");
        sb.append("\tpublic List<").append(this.relateEntityMetadata.getEntityClassName()).append("> listByIds(Collection<Long> ids) {\n");
        sb.append("\t\tvar sql = \"SELECT * FROM `").append(this.relateEntityMetadata.getRelateTableName()).append("` WHERE `id` IN (:ids)\";\n\n");
        sb.append("\t\treturn this.jdbcClient\n");
        sb.append("\t\t\t\t.sql(sql)\n");
        sb.append("\t\t\t\t.param(\"ids\", ids)\n");
        sb.append("\t\t\t\t.query(this.buildRowMapper())\n");
        sb.append("\t\t\t\t.list();\n");
        sb.append("\t}\n\n");

        // 如果含有is_deleted字段，则需要重写deleteById方法
        if (this.relateEntityMetadata.getHasDeletedField()) {
            // deleteById() 方法
            sb.append("\t@Override\n");
            sb.append("\t@SuppressWarnings(\"Duplicates\")\n");
            sb.append("\tpublic int deleteById(Long id) {\n");
            sb.append("\t\tvar sql = \"\"\"\n");
            sb.append("\t\t\t\tUPDATE `").append(this.relateEntityMetadata.getRelateTableName()).append("`\n");
            sb.append("\t\t\t\tSET `is_deleted` = :deleted, `update_time` = :updateTime\n");
            sb.append("\t\t\t\tWHERE `id` = :id\n");
            sb.append("\t\t\t\t\"\"\";\n\n");
            sb.append("\t\treturn this.jdbcClient\n");
            sb.append("\t\t\t\t.sql(sql)\n");
            sb.append("\t\t\t\t.param(\"id\", id)\n");
            sb.append("\t\t\t\t.param(\"deleted\", Boolean.TRUE)\n");
            sb.append("\t\t\t\t.param(\"updateTime\", LocalDateTime.now())\n");
            sb.append("\t\t\t\t.update();\n");
            sb.append("\t}\n\n");

            // deleteByIds() 方法
            sb.append("\t@Override\n");
            sb.append("\t@SuppressWarnings(\"Duplicates\")\n");
            sb.append("\tpublic int deleteByIds(Long id) {\n");
            sb.append("\t\tvar sql = \"\"\"\n");
            sb.append("\t\t\t\tUPDATE `").append(this.relateEntityMetadata.getRelateTableName()).append("`\n");
            sb.append("\t\t\t\tSET `is_deleted` = :deleted, `update_time` = :updateTime\n");
            sb.append("\t\t\t\tWHERE `id` IN (:ids)\n");
            sb.append("\t\t\t\t\"\"\";\n\n");
            sb.append("\t\treturn this.jdbcClient\n");
            sb.append("\t\t\t\t.sql(sql)\n");
            sb.append("\t\t\t\t.param(\"ids\", ids)\n");
            sb.append("\t\t\t\t.param(\"deleted\", Boolean.TRUE)\n");
            sb.append("\t\t\t\t.param(\"updateTime\", LocalDateTime.now())\n");
            sb.append("\t\t\t\t.update();\n");
            sb.append("\t}\n\n");
        }

        // daoImpl类定义结束
        sb.append("}");

        return sb.toString();
    }

    @SuppressWarnings("unused")
    public String getFileName() {
        return fileName;
    }

    @SuppressWarnings("unused")
    public String getPackageName() {
        return packageName;
    }

    @SuppressWarnings("unused")
    public String getPackageStatement() {
        return packageStatement;
    }

    @SuppressWarnings("unused")
    public String getClassName() {
        return className;
    }

    @SuppressWarnings("unused")
    public String getImplementInterfaceName() {
        return implementInterfaceName;
    }

    @SuppressWarnings("unused")
    public Set<String> getImportStatements() {
        return importStatements;
    }

    @SuppressWarnings("unused")
    public EntityMetadata getRelateEntityMetadata() {
        return relateEntityMetadata;
    }
}
