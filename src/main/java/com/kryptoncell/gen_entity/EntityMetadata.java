package com.kryptoncell.gen_entity;

import com.kryptoncell.gen_db.TableColumnMetadata;

import java.util.List;
import java.util.Map;

public final class EntityMetadata {
    private final String packageName; // entity类的包名
    private final List<String> importStatements; // entity类的import语句
    private final String entityClassName; // entity类的类名
    private final List<EntityFieldMetadata> fields; // entity所有的成员变量

    public EntityMetadata(String dbTable, List<TableColumnMetadata> dbColumns) {

    }

    @SuppressWarnings("unused")
    public String getPackageName() {
        return packageName;
    }

    @SuppressWarnings("unused")
    public List<String> getImportStatements() {
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
}
