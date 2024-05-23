package com.kryptoncell.gen_entity;

import java.util.List;

public record EntityMetadata(
        String packageName, // entity类的包名
        List<String> importStatements, // entity类的import语句
        String entityClassName, // entity类的类名
        List<EntityFieldMetadata> fields // entity所有的成员变量
) {}
