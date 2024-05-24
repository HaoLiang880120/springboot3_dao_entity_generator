package com.kryptoncell.gen_dao;

public final class BaseDaoMetadata {

    private final String packageName;
    private final String packageStatement;

    public BaseDaoMetadata(String basePackage) {
        this.packageName = basePackage + ".dao";
        this.packageStatement = "import " + this.packageName + ";";
    }

    @SuppressWarnings("unused")
    public String getPackageName() {
        return packageName;
    }

    @SuppressWarnings("unused")
    public String getPackageStatement() {
        return packageStatement;
    }

    public String toFileWriteString() {

        return this.packageStatement + "\n\n" +

                // import语句
                "import java.util.Collection;\n" +
                "import java.util.List;\n" +
                "import java.util.Optional;\n\n" +

                // 接口注释
                "/**\n" +
                " * Dao根接口，所有的Dao接口都应该继承此接口。\n" +
                " * @param <PK> 泛型，主键的类型\n" +
                " * @param <E> 泛型，实体的类型\n" +
                " */\n" +

                // 接口定义
                "public interface _BaseDao <PK, E> {\n" +

                // saveBatch()方法
                "\t/**\n" +
                "\t * 将数据批量插入表\n" +
                "\t * 注: 当需要对单张表一次性插入多条数据时，请使用此方法，能获得更好的性能。此方法与save()方法不同，\n" +
                "\t * \t插入成功后，entity列表元素无法通过entity.getId()获得主键值。\n" +
                "\t */\n" +
                "\t @SuppressWarnings(\"unused, UnusedReturnValue\")\n" +
                "\t void saveBatch(Collection<E> entities);\n\n" +

                // save()方法
                "\t/**\n" +
                "\t * 将单条数据插入表\n" +
                "\t * 注: 插入表后数据库自动生成的主键id，会赋值到传入方法的entity.id上。调用此save()方法后，\n" +
                "\t * \t可以直接通过entity.getId()获得主键值。\n" +
                "\t */\n" +
                "\t@SuppressWarnings(\"unused, UnusedReturnValue\")\n" +
                "\tvoid save(E entity);\n\n" +

                // getById()方法
                "\t/**\n" +
                "\t * 通过主键查询单条数据\n" +
                "\t */\n" +
                "\tOptional<E> getById(PK id);\n\n" +

                // listByIds() 方法
                "\t/**\n" +
                "\t * 通过主键列表查询多条数据\n" +
                "\t */\n" +
                "\t@SuppressWarnings(\"unused\")\n" +
                "\tList<E> listByIds(Collection<PK> ids);\n\n" +

                // deleteById() 方法
                "\t/**\n" +
                "\t * 通过主键删除单条数据(软删除)\n" +
                "\t * 返回值: 数据库表影响的行数\n" +
                "\t * 注：有的数据库表没有`is_deleted`字段，就不需要实现此方法。\n" +
                "\t */\n" +
                "\t@SuppressWarnings(\"unused, UnusedReturnValue\")\n" +
                "\tdefault int deleteById(PK id) {\n" +
                "\t\treturn 0;\n" +
                "\t}\n" +

                // deleteByIds() 方法
                "\t/**\n" +
                "\t * 通过主键列表查询删除数据(软删除)\n" +
                "\t * 返回值: 数据库表影响的行数\n" +
                "\t * 注：有的数据库表没有`is_deleted`字段，就不需要实现此方法。\n" +
                "\t */\n" +
                "\t@SuppressWarnings(\"unused, UnusedReturnValue\")\n" +
                "\tdefault int deleteByIds(Collection<PK> ids) {\n" +
                "\t\treturn 0;\n" +
                "\t}\n" +

                // 接口定义结束
                "}";
    }
}
